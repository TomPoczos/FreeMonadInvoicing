package algebras

import Model._
import cats.InjectK
import cats.free.Free
import cats.free.Free._

object Accounts {

  sealed trait AccountOp[A]

  case object Create extends AccountOp[AccountId]

  case class Find(id: AccountId) extends AccountOp[Account]

  object AccountOp {
    type AccountOpF[A] = Free[AccountOp, A]

    def create: AccountOpF[AccountId] =
      liftF[AccountOp, AccountId](Create)

    def find(id: AccountId): AccountOpF[Account] =
      liftF[AccountOp, Account](Find(id))
  }

  class AccountOpI[F[_]](implicit I: InjectK[AccountOp, F]) {

    def create: Free[F, AccountId] =
      inject[AccountOp, F](Create)

    def find(id: AccountId): Free[F, Account] =
      inject[AccountOp, F](Find(id))
  }

  object Implicits {
    implicit def accountOpI[F[_]](implicit I: InjectK[AccountOp, F]) = new AccountOpI
  }
}
