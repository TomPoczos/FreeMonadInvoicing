package algebras

import Model._
import cats.InjectK
import cats.free.Free
import cats.free.Free._

object Balances {

  sealed trait BalanceOp[A]

  case class Create(amount: Int, acountId: AccountId, date: String)
      extends BalanceOp[BalanceId]

  case class FindLast(acountId: AccountId) extends BalanceOp[Balance]

  case class FindLastForDate(acountId: AccountId, date: String)
      extends BalanceOp[Balance]

  object BalanceOp {

    type BalanceOpF[A] = Free[BalanceOp, A]

    def create(
        amount: Int,
        acountId: AccountId,
        date: String
    ): BalanceOpF[BalanceId] =
      liftF[BalanceOp, BalanceId](Create(amount, acountId, date))

    def findLast(accountId: AccountId): BalanceOpF[Balance] =
      liftF[BalanceOp, Balance](FindLast(accountId))

    def findLastForDate(
        accountId: AccountId,
        date: String
    ): BalanceOpF[Balance] =
      liftF[BalanceOp, Balance](FindLast(accountId))

  }

  class BalanceOpI[F[_]](implicit I: InjectK[BalanceOp, F]) {

    def create(
        amount: Int,
        acountId: AccountId,
        date: String
    ): Free[F, BalanceId] =
      inject[BalanceOp, F](Create(amount, acountId, date))

    def findLast(accountId: AccountId): Free[F, Balance] =
      inject[BalanceOp, F](FindLast(accountId))

    def findLastForDate(accountId: AccountId, date: String): Free[F, Balance] =
      inject[BalanceOp, F](FindLastForDate(accountId, date))

  }

  object Implicits {

    implicit def balanceOpI[F[_]](implicit I: InjectK[BalanceOp, F]) =
      new BalanceOpI
  }
}
