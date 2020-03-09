package interpreters

import Model._
import algebras.Balances.BalanceOp.BalanceOpF
import algebras.Balances.{BalanceOp, _}
import cats.implicits._
import cats.~>
import doobie.free.connection.ConnectionIO
import doobie.implicits._

object Balances {

  val interpreter: BalanceOp ~> ConnectionIO =
    λ[BalanceOp ~> ConnectionIO](
      _ match {
        case Create(amount, AccountId(accId), date) =>
          sql"""
               | insert into balance (amount, account_fk, date)
               | values ($amount, $accId, $date)
               |""".stripMargin.update.run *>
            sql"SELECT last_insert_rowid()".query[BalanceId].unique
        case FindLast(AccountId(accId)) =>
          sql"""
               | select id, amount, account_fk, date 
               | from balance
               | where account_fk = $accId
               | order by date desc
               | limit 1 
               |""".stripMargin.query[Balance].unique
        case FindLastForDate(AccountId(accId), date) =>
          sql"""
               | select id, amount, account_fk, date 
               | from balance
               | where account_fk = $accId
               | and date <= $date
               | order by date desc
               | limit 1 
               |""".stripMargin.query[Balance].unique

      }
    )

  object Implicits {

    implicit class BalanceInterpreterOps[A](account: BalanceOpF[A]) {
      def interpret = account.foldMap(interpreter)
    }
  }

}

// object Accounts {
//   val interpreter: AccountOp ~> ConnectionIO =
//     λ[AccountOp ~> ConnectionIO](
//       _ match {
//         case Create =>
//           sql"insert into Account default values".update.run *>
//             sql"SELECT last_insert_rowid()".query[AccountId].unique
//         case Find(AccountId(accountId)) =>
//           sql"select id, balance from account where id = $accountId".query[Account].unique
//       }
//     )

//   object Implicits {
//     implicit class AccountInterpreterOps[A](account: AccountOpF[A]) {
//       def interpret = account.foldMap(interpreter)
//     }
//   }
// }
