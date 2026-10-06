package com.scallop.ledger.service.helper;


import java.math.*;
import java.util.*;

import com.scallop.ledger.constant.PostingType;
import com.scallop.ledger.exception.LedgerException;
import com.scallop.ledger.exception.LedgerInvariantViolationException;
import com.scallop.ledger.request.RequestData.JournalRequest;
import com.scallop.ledger.request.RequestData.PostingLeg;

/** Independently implemented accounting patterns from PDF §§2,3,5. No upstream source copied. */
public final class JournalFactory {
  public static final BigDecimal ZERO = new BigDecimal("0.000000"),
      ONE = new BigDecimal("1.000000");
  public static final Set<String> SWAPS =
      Set.of("CRYPTO_CRYPTO", "CRYPTO_FIAT", "FIAT_CRYPTO", "FIAT_FIAT");
  public static final Set<String> PENDING_TYPES =
      Set.of("WALLET_SEND", "WITHDRAWAL", "FIAT_SEND", "QR_PAYMENT", "CARD_SPENT");
  public static final Set<String> ROOT_ACCOUNTS =
      Set.of(
          "USER_AVAILABLE_BALANCE",
          "VIRTUAL_CARD_LEDGER",
          "BLOCKCHAIN_SETTLEMENT_RAIL",
          "BANK_SETTLEMENT_RAIL",
          "FX_SWAP_ORACLE_RAIL",
          "VISA_CARD_SETTLEMENT_RAIL",
          "MERCHANT_QR_SETTLEMENT_RAIL",
          "ESIM_TELECOM_SETTLEMENT_RAIL",
          "INTERNAL_P2P_TRANSFER_RAIL",
          "SETTLEMENT_REFUND_RAIL",
          "SWAP_SPREAD_FEE",
          "WITHDRAWAL_SERVICE_FEE",
          "PAYOUT_FEE",
          "PAYIN_FEE",
          "VIRTUAL_CARD_FEE",
          "MEMBERSHIP_SUBSCRIPTION_FEE",
          "P2P_TRANSFER_FEE",
          "ESIM_SERVICE_FEE",
          "QR_PAYMENT_FEE",
          "QR_PROCESSING_FEE",
          "ON_CHAIN_GAS_FEE_POOL",
          "PROMOTIONAL_REWARD_POOL");

  public record Plan(List<PostingLeg> legs, BigDecimal netAmount, BigDecimal rate, String kind) {}

  public static BigDecimal money(BigDecimal n, boolean positive) {
    if (n == null) throw LedgerException.invalid("Amount required");
    try {
      n = n.setScale(6, RoundingMode.UNNECESSARY);
    } catch (ArithmeticException e) {
      throw LedgerException.invalid("Frozen architecture permits at most six decimal places");
    }
    if (n.precision() > 18 || n.signum() < 0 || (positive && n.signum() == 0))
      throw LedgerException.invalid("Amount outside NUMERIC(18,6) domain");
    return n;
  }

  public static String user(String id) {
    if (id == null || !id.matches("[A-Za-z0-9._@-]{1,100}"))
      throw LedgerException.invalid("Valid user ID required");
    return "LIABILITY:USER_AVAILABLE_BALANCE [" + id + "]";
  }

  public static String card(String id) {
    user(id);
    return "LIABILITY:VIRTUAL_CARD_LEDGER [" + id + "]";
  }

  public static String rail(String name, String c) {
    return "CLEARING:" + name + " (" + c + ")";
  }

  public static String revenue(String name, String c) {
    return "REVENUE:" + name + " (" + c + ")";
  }

  public static String outboundRail(JournalRequest r) {
    return rail(
        switch (r.transactionType()) {
          case "WALLET_SEND", "WITHDRAWAL" -> "BLOCKCHAIN_SETTLEMENT_RAIL";
          case "FIAT_SEND" -> "BANK_SETTLEMENT_RAIL";
          case "QR_PAYMENT" -> "MERCHANT_QR_SETTLEMENT_RAIL";
          case "CARD_SPENT" -> "VISA_CARD_SETTLEMENT_RAIL";
          default -> throw LedgerException.invalid("Unsupported pending event");
        },
        r.baseCurrency()
            + (Set.of("WALLET_SEND", "WITHDRAWAL").contains(r.transactionType())
                ? " - " + requiredNetwork(r)
                : ""));
  }

  private static String requiredNetwork(JournalRequest r) {
    if (r.network() == null || !r.network().matches("[A-Z0-9_-]{1,32}"))
      throw LedgerException.invalid("Blockchain network required");
    return r.network();
  }

  private static void leg(
      List<PostingLeg> a,
      String account,
      String c,
      PostingType type,
      BigDecimal amt,
      String description) {
    if (amt.signum() != 0) a.add(new PostingLeg(account, c, type, money(amt, true), description));
  }

  public static void validateZeroSumEquilibrium(List<PostingLeg> legs) {
    if (legs == null || legs.size() < 2 || legs.size() > 64)
      throw new LedgerInvariantViolationException("Journal requires 2 to 64 nonzero posting legs");
    Map<String, BigDecimal> delta = new TreeMap<>();
    for (PostingLeg l : legs) {
      if (l == null
          || l.type() == null
          || l.account() == null
          || l.currency() == null
          || !l.currency().matches("[A-Z0-9]{2,16}"))
        throw new LedgerInvariantViolationException("Invalid posting fields");
      String root =
          l.account().split(":", 2).length == 2
              ? l.account().split(":", 2)[1].split("[ \\[]", 2)[0]
              : "";
      if (!ROOT_ACCOUNTS.contains(root)
          || !l.account().matches("^(LIABILITY|CLEARING|REVENUE|EXPENSE):.*")
          || l.account().length() > 255)
        throw new LedgerInvariantViolationException("Unregistered account taxonomy");
      BigDecimal n = money(l.amount(), true);
      delta.merge(l.currency(), l.type() == PostingType.DR ? n : n.negate(), BigDecimal::add);
    }
    if (delta.values().stream().anyMatch(n -> n.signum() != 0))
      throw new LedgerInvariantViolationException("Journal out of equilibrium: " + delta);
  }

  public static Plan create(JournalRequest r, BigDecimal suppliedRate) {
    String c = r.baseCurrency(),
        target = r.targetCurrency() == null ? c : r.targetCurrency(),
        t = r.transactionType();
    if (t.equals("WITHDRAWAL")) t = "WALLET_SEND";
    if (c == null || !c.matches("[A-Z0-9]{2,16}") || !target.matches("[A-Z0-9]{2,16}"))
      throw LedgerException.invalid("Asset required");
    BigDecimal gross = money(r.grossAmount(), true),
        fee = money(r.feeAmount() == null ? ZERO : r.feeAmount(), false),
        gas = money(r.gasAmount() == null ? ZERO : r.gasAmount(), false);
    if (gas.signum() > 0 && !t.equals("WALLET_SEND"))
      throw LedgerException.invalid("Gas fee applies only to WALLET_SEND");
    if (fee.add(gas).compareTo(gross) >= 0)
      throw LedgerException.invalid("Fees must be below gross amount");
    BigDecimal net = gross.subtract(fee).subtract(gas),
        rate = suppliedRate == null ? ONE : money(suppliedRate, true);
    List<PostingLeg> a = new ArrayList<>();
    String u = user(r.userId());
    if (!SWAPS.contains(t) && !c.equals(target))
      throw LedgerException.invalid("Non-swap event cannot silently change the target asset");
    if (t.equals("CARD_SPENT") && fee.signum() > 0)
      throw LedgerException.invalid("Card hold and capture must not contain unsupported fees");
    if ("PENDING".equals(r.status())) {
      if (!PENDING_TYPES.contains(t))
        throw LedgerException.invalid("Only outbound/card events can be pending");
      leg(
          a,
          t.equals("CARD_SPENT") ? card(r.userId()) : u,
          c,
          PostingType.DR,
          gross,
          "Reserve available funds");
      leg(a, outboundRail(r), c, PostingType.CR, gross, "Pending outbound clearing; not external settlement");
      validateZeroSumEquilibrium(a);
      return new Plan(List.copyOf(a), net, rate, "HOLD");
    }
    if (!Set.of("COMPLETED", "CONFIRMED").contains(r.status()))
      throw LedgerException.invalid("Only confirmed/completed events post final journals");
    if (SWAPS.contains(t)) {
      if (c.equals(target)) throw LedgerException.invalid("Swap requires different assets");
      BigDecimal out = money(net.multiply(rate).setScale(6, RoundingMode.HALF_EVEN), true);
      if (r.expectedTargetAmount() != null
          && money(r.expectedTargetAmount(), true).compareTo(out) != 0)
        throw LedgerException.invalid("Target amount differs from fee-adjusted quoted rate");
      leg(a, u, c, PostingType.DR, gross, "Source customer liability debited");
      leg(a, rail("FX_SWAP_ORACLE_RAIL", c + " - " + target), c, PostingType.CR, net, "Source FX liquidity");
      leg(a, revenue("SWAP_SPREAD_FEE", c), c, PostingType.CR, fee, "Source-currency spread revenue");
      leg(a, rail("FX_SWAP_ORACLE_RAIL", target), target, PostingType.DR, out, "Target FX liquidity");
      leg(a, u, target, PostingType.CR, out, "Target customer liability credited");
      validateZeroSumEquilibrium(a);
      return new Plan(List.copyOf(a), out, rate, "POST");
    }
    switch (t) {
      case "WALLET_RECEIVE", "DEPOSIT" -> {
        if (fee.signum() > 0) throw LedgerException.invalid("Crypto deposit fee must be zero");
        leg(
            a,
            rail("BLOCKCHAIN_SETTLEMENT_RAIL", c + " - " + requiredNetwork(r)),
            c,
            PostingType.DR,
            gross,
            "Confirmed custody receipt");
        leg(a, u, c, PostingType.CR, gross, "Customer crypto deposit");
      }
      case "FIAT_RECEIVE" -> {
        leg(a, rail("BANK_SETTLEMENT_RAIL", c), c, PostingType.DR, gross, "Confirmed bank receipt");
        leg(a, u, c, PostingType.CR, net, "Net customer pay-in");
        leg(a, revenue("PAYIN_FEE", c), c, PostingType.CR, fee, "Pay-in revenue");
      }
      case "WALLET_SEND", "WITHDRAWAL" -> {
        leg(a, u, c, PostingType.DR, gross, "Gross customer withdrawal");
        leg(a, outboundRail(r), c,PostingType.CR, net, "Net blockchain settlement");
        leg(
            a,
            "EXPENSE:ON_CHAIN_GAS_FEE_POOL (" + c + " - " + requiredNetwork(r) + ")",
            c,
            PostingType.CR,
            gas,
            "Gas pool reimbursement in withdrawal asset; actual native gas costs reconcile"
                + " separately");
        leg(a, revenue("WITHDRAWAL_SERVICE_FEE", c), c, PostingType.CR, fee, "Platform margin");
      }
      case "FIAT_SEND", "QR_PAYMENT", "ESIM_PURCHASE_SENT", "ESIM_TOPUP_SENT" -> {
        String railName =
            t.equals("FIAT_SEND")
                ? "BANK_SETTLEMENT_RAIL"
                : t.equals("QR_PAYMENT")
                    ? "MERCHANT_QR_SETTLEMENT_RAIL"
                    : "ESIM_TELECOM_SETTLEMENT_RAIL";
        String feeName =
            t.equals("FIAT_SEND")
                ? "PAYOUT_FEE"
                : t.equals("QR_PAYMENT") ? "QR_PAYMENT_FEE" : "ESIM_SERVICE_FEE";
        leg(a, u, c, PostingType.DR, gross, "Customer outbound gross");
        leg(a, rail(railName, c), c, PostingType.CR, net, "Net partner settlement");
        leg(a, revenue(feeName, c), c, PostingType.CR, fee, "Service revenue");
      }
      case "VIRTUAL_CARD_ADD_FUNDS" -> {
        if (!target.equals(c))
          throw LedgerException.invalid(
              "Perform separate atomic FX swap before card funding; card load uses one currency per"
                  + " frozen matrix");
        leg(a, u, c, PostingType.DR, gross, "Customer available balance debited");
        leg(a, card(r.userId()), c, PostingType.CR, net, "Prefunded card balance");
        leg(a, revenue("VIRTUAL_CARD_FEE", c), c, PostingType.CR, fee, "Card fee");
      }
      case "CARD_SPENT" -> {
        if (fee.signum() > 0)
          throw LedgerException.invalid(
              "Book issuer fees separately; CARD_SPENT matrix has no fee leg");
        if (!target.equals(c))
          throw LedgerException.invalid(
              "Convert card funds to settlement currency before CARD_SPENT");
        leg(a, card(r.userId()), c, PostingType.DR, gross, "Card authorization/capture");
        leg(a, rail("VISA_CARD_SETTLEMENT_RAIL", c), c, PostingType.CR, gross, "Visa clearing");
      }
      case "SCALLOP_ID_SEND" -> {
        if (r.counterpartyUserId() == null || r.userId().equals(r.counterpartyUserId()))
          throw LedgerException.invalid("Different P2P recipient required");
        user(r.counterpartyUserId());
        leg(a, u, c, PostingType.DR, gross, "P2P sender gross");
        leg(a, rail("INTERNAL_P2P_TRANSFER_RAIL", c), c, PostingType.CR, net, "P2P pending recipient credit");
        leg(a, revenue("P2P_TRANSFER_FEE", c), c, PostingType.CR, fee, "Optional P2P fee");
      }
      case "SCALLOP_ID_RECEIVE" -> {
        if (fee.signum() > 0) throw LedgerException.invalid("P2P receive fee must be zero");
        leg(a, rail("INTERNAL_P2P_TRANSFER_RAIL", c), c, PostingType.DR, gross, "P2P recipient settlement");
        leg(a, u, c, PostingType.CR, gross, "P2P recipient credited");
      }
      case "QR_RECEIVE" -> {
        leg(a, rail("MERCHANT_QR_SETTLEMENT_RAIL", c), c, PostingType.DR, gross, "Merchant QR receipt");
        leg(a, u, c, PostingType.CR, net, "Merchant net liability");
        leg(a, revenue("QR_PROCESSING_FEE", c), c, PostingType.CR, fee, "QR processing revenue");
      }
      case "WALLET_REFUND", "FIAT_REFUND" ->
          throw LedgerException.invalid(
              "Use linked reversal endpoint; unbacked standalone refunds prohibited");
      case "TIER_SUBSCRIPTION" -> {
        if (fee.signum() > 0)
          throw LedgerException.invalid("Subscription gross is the fee; feeAmount must be zero");
        leg(a, u, c, PostingType.DR, gross, "Membership subscription");
        leg(a, revenue("MEMBERSHIP_SUBSCRIPTION_FEE", c), c, PostingType.CR, gross, "Membership revenue");
        net = ZERO;
      }
      case "REFERRAL_EARNING_REDEMPTION" -> {
        if (fee.signum() > 0) throw LedgerException.invalid("Reward fee must be zero");
        leg(
            a,
            "EXPENSE:PROMOTIONAL_REWARD_POOL (" + c + ")",
            c,
            PostingType.DR,
            gross,
            "Approved promotional expense");
        leg(a, u, c, PostingType.CR, gross, "Promotional reward");
      }
      default -> throw LedgerException.invalid("Unsupported transaction type: " + t);
    }
    validateZeroSumEquilibrium(a);
    return new Plan(List.copyOf(a), net, rate, "POST");
  }
}
