package algebras

import Model._
import cats.InjectK
import cats.free.Free
import cats.free.Free._

object Payments {

  sealed trait PaymentOp[A]

  case class Create(amount: Int, invoiceId: InvoiceId, date: String) 
      extends PaymentOp[PaymentId]

  type PaymentOpF[A] = Free[PaymentOp, A]

  class PaymentOpI[F[_]](implicit I: InjectK[PaymentOp, F]) {
    def create(amount: Int, invoice_fk: InvoiceId, date: String): Free[F, PaymentId] =
      inject[PaymentOp, F](Create(amount, invoice_fk, date))
  }

  object Implicits {
    implicit def paymentOpI[F[_]](implicit I: InjectK[PaymentOp, F]) = 
      new PaymentOpI
  }
}
