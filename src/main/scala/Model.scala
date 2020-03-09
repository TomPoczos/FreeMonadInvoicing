package object Model {

  case class Account(account: AccountId)

  case class AccountId(value: Int)

  case class Invoice(
      id: InvoiceId,
      amount: Int,
      accountId: AccountId,
      date: String
  )

  case class InvoiceId(value: Int)

  case class Payment(
      id: PaymentId,
      amount: Int,
      invoiceId: InvoiceId,
      date: String
  )

  case class PaymentId(value: Int)

  case class Balance(id: BalanceId, amount: Int)

  case class BalanceId(value: Int)
}
