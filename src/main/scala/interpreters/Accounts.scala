package interpreters

import Model._
import algebras.Accounts.AccountOpF
import algebras.Accounts._
import cats.implicits._
import cats.~>
import doobie.free.connection.ConnectionIO
import doobie.implicits._

object Accounts {
  val interpreter: AccountOp ~> ConnectionIO =
    λ[AccountOp ~> ConnectionIO](
      _ match {
        case Create =>
          sql"insert into Account default values".update.run *>
            sql"SELECT last_insert_rowid()".query[AccountId].unique
        case Find(AccountId(accountId)) =>
          sql"select id, balance from account where id = $accountId".query[Account].unique
      }
    )

  object Implicits {
    implicit class AccountInterpreterOps[A](account: AccountOpF[A]) {
      def interpret = account.foldMap(interpreter)
    }
  }
}
