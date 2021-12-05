package pl.scala.exercises

import pl.scala.exercises.model._

import java.time.LocalDate
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.postfixOps

final class Exercises(actions: Actions) {
  /**
    * TODO Ex1
    * Implement function for getting emails of all employees.
    */
  def employeesEmails: List[String] = CompanyRepository
      .employees
      .map(x=> x.email)

  /**
    * TODO Ex3
    * Implement function for getting tuples of full name and email of all employees. Use method fullName from Employee class.
    */
  def employeesNamesWithEmails: List[(String, String)] = CompanyRepository
    .employees
    .map(e => (e.fullName, e.email))

  /**
    * TODO Ex4
    * Implement function returning list of tuples containing list of employee names (fullName) and phone for every phone of each employee.
    *
    * HINT: use flatMap or flatten
    */
  def getAllPhones: List[(String, String)] = CompanyRepository
    .employees
    .map(e => e.phones.map((e.fullName, _)))
    .flatten // _ to jest pierwszy argument wiec mozna go przekazac przez _ po prostu

  /**
    * TODO Ex5
    * Return list of string containing first and last name of employee sorted alphabetically by the last name.
    */
  def sortedEmployeesNames: List[String] = CompanyRepository
    .employees
    .sortBy(_.lastName)     //moze byc sortWith
    .map(_.fullName)

  /**
    * TODO Ex6
    * Return list of employees as string in following format:
    * 1. John Steward
    * 2. Jane Doe
    * ....
    *
    * HINT: Use zipWithIndex and mkString
    */
  def listOfEmployeesNames: String =
    CompanyRepository
      .employees
      .sortBy(_.lastName)
      .zip(LazyList.from(1))
//      .map{
//        case (emp, idx) =>
//          s"$idx. ${emp.fullName}"
//      }
//      .map(e => {
//        s"${e._2}. ${e._1.fullName} "
//      })        //mozna od razu nawiasy klamrowe tez jak na dole dac
      .map{e =>
        s"${e._2}. ${e._1.fullName}"
      }
      .mkString("\n")



  /**
    * TODO Ex7
    * Return list of employees as string in following format:
    * A. John Steward
    * B. Jane Doe
    * ....
    *
    * HINT: Use range, zip and mkString
    */
  def listOfEmployeesNamesWithLetters: String = CompanyRepository
    .employees
    .sortBy(_.lastName)
    .zip('A' to 'Z')
    .map{e =>
      s"${e._2}. ${e._1.fullName}"
    }
    .mkString("\n")

  /**
    * TODO Ex8b
    * Implement function to get maximum and minimum value of salary and return it as an option of tuple (minimum, maximum).
    * Return None is passed list is empty
    */
  def minAndMaxSalary(employees: List[Employee]): Option[(Salary, Salary)] = {
    val sorted = employees.map(_.salary).sortBy(_.value)

//    sorted match {
//      case List() => None
//      case l => Some(l.head, l.last)
//    }       TO PIERWSZE ROZWIAZANIE

    val maybeHead = sorted.headOption
    val maybeLast = sorted.lastOption
//    (maybeHead, maybeLast) match {
//      case (Some(h), Some(l)) => Some((h,l))
//      case _ => None
//    }       DRUGIE ROZWIAZANIE

    maybeHead.flatMap(h => maybeLast.map(l => (h,l)))

  }


  /**
    * TODO Ex9
    * Implement function to modify salaries in the list by multiplying value by modifier. Apply modifier1 for lowest 2, and modifier2 for the rest.
    */
  def modifySalaries(salaries: List[Salary], modifier1: Double, modifier2: Double): List[Salary] = {

//    def modify(v: Double)(salary: Salary): Salary = salary.copy(value = (salary.value * v).toInt)
//    val sorted = salaries.sorted
//    sorted.dropRight(2).map(modify(modifier2)) ++ sorted.takeRight(2).map(modify(modifier1))

//    val sorted = salaries.sorted
//    val result = sorted.dropRight(2).map(_.value * modifier1)
//    val result2 = sorted.takeRight(2).map(_.value * modifier2)
//    List(result, result2).flatten

    def modify(v: Double)(salary: Salary): Salary = salary.copy(value = (v * salary.value).toInt)
    val sorted = salaries.sorted

    sorted.take(2).map(s => modify(modifier1)(s)) ++ sorted.drop(2).map(modify(modifier2))
  }

  /**
    * TODO Ex11
    * Implement function to two first best earning employees return it as an option of tuple (minimum, maximum).
    */
  def twoBestEarningEmployees: Option[(Employee, Employee)] = ???

  /**
    * TODO Ex13
    * Return list of employees that are active today.
    */
  def getAllActiveEmployees: List[Employee] =
    CompanyRepository
      .employees
      .filter(_.isActiveAt(LocalDate.now()))

  /**
    * TODO Ex16
    * Implement function for getting tuples of employees email and employee instance by domain passed as argument.
    */
  def findUsersByDomain(domain: String): List[(String, Employee)] =
    CompanyRepository
      .employees
//      .flatMap {
//        case employee @ EmailDomain(`domain`, email) => Some((email, employee)) // ta @ lapie caly kontekst w pattern matchingu
//        case _ => None
//      }
      .collect{
        case employee @ EmailDomain(`domain`, email) => (email, employee)       //collect robi od razu filter i map wiec mniej kodu
      }

  /**
    * TODO Ex17b
    * Implement function that return sum of salaries of all top level managers
    */

  def getSumOfTopLevelManagersSalaries(): Int = {
    CompanyRepository
      .employees
      .collect{
        case TopLevelManager(e) => e.salary.value
      }.sum
  }

  /**
    * TODO Ex18
    * Implement function that sends a mail to all employees with departmentId passed as argument.
    *
    * HINT: Use foreach
    */

  def sendMailToAllEmployeesOfDepartment(departmentId: Int, message: String): Unit = {
    CompanyRepository
      .employees
      .filter(_.getActiveDepartment.exists(_.id == departmentId))
      .foreach(e => actions.sendMail(e.email, "hello"))
//
//    CompanyRepository.employees.collect{
//      case e if e.getActiveDepartment.exists(_.id == departmentId) => e.email
//    }.foreach(actions.sendMail(_, message))     DRUGI SPOSOB
  }


  /**
    * TODO Ex19
    * Split employees and return tuple containing 3 lists of employees:
    *  1st list of employees having more than 1 phone
    *  2nd list of employees having more than 1 employment period
    *  3rd list for the rest
    *
    *  HINT: Use foldLeft
    */
  def splitEmployees: (List[Employee], List[Employee], List[Employee]) = {
    CompanyRepository
      .employees
      .foldLeft((
        List.empty[Employee],
        List.empty[Employee],
        List.empty[Employee]
      )) {
        case ((moreThan1Phone, modeThan1EmpPeriod, rest), employee) =>
          employee match {
            case e if e.phones.size > 1 => (moreThan1Phone :+ e, modeThan1EmpPeriod, rest)
            case e if e.employmentHistory.size > 1 => (moreThan1Phone, modeThan1EmpPeriod :+ e, rest)
            case e => (moreThan1Phone, modeThan1EmpPeriod, rest :+ e)
          }
      }
  }

  /**
    * TODO Ex20
    * Implement function returning office location of employee as "<building>: <office>" but returning Some only
    * if employee has office.
    * HINT: use flatMap
    */
  def getOfficeOfEmployee(e: Employee): Option[String] =
//    e.location
//      .flatMap(l =>
//        l.office.map(
//          o => s"${l.building}:$o"
//      ))    to jest na flatmapie, a mozna zrobic na for-comprehension jak na dole

  for {
    location <- e.location
    office <- location.office
  } yield s"${location.building}:$office"


  /**
    * TODO Ex22
    * Implement function that returns all possible pairs of employees names passed as argument.
    * For example for input:
    * Rick, Brooke, Terry
    * return
    * (Rick,Brooke), (Rick,Terry), (Brooke,Rick), (Brooke,Terry), (Terry,Rick), (Terry,Brooke)
    * HINT: use flatMap
    */
  def getPairsOfEmployees(employeeNames: List[String]): List[(String, String)] =
    for {
    e1 <- employeeNames
    e2 <- employeeNames if e1 != e2
  } yield (e1, e2)      //for-comprehension

//  employeeNames.flatMap(left =>
//  employeeNames.flatMap(right => {
//    if (left != right) {
//      List((left, right))
//    } else {
//      Nil
//    }
//  }))

  /**
    * TODO Ex21
    * Implement ordering for localData
    */
  implicit val ordering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  /**
    * TODO Ex24
    * Return list of sorted employees from latest hired. If employee has no employment period they should go last.
    */
  def sortedEmployeesFromNewest: List[Employee] = ???

  /**
    * TODO Ex25
    * Implement function taking salary as argument and returning tuple of two lists:
    * all employees below given salary and all employees above.
    */
  def aboveAndBelowSalary(s: Int): (List[Employee], List[Employee]) = ???

  /**
    * TODO Ex26
    * Implement function that returns the sum of the days employee was employed across all periods for today.
    */
  def employmentDays(employee: Employee): Long = ???

  /**
    * TODO Ex27
    * Implement function returning map of department as key and list of active employees as value.
    * HINT: use method view on map.
    */

  def employeesByDepartment: Map[Department, List[Employee]] = ???

  /**
    * TODO Ex28
    * Calculate average salary per department. Reuse method employeesByDepartment.
    * HINT: use method view on map.
    */
  def salariesByDepartment: Map[Department, Int] = ???

  /**
    * TODO Ex31
    * Get all available resources for user. Use method canAccessResource from user.
    */
  def getAllAvailableResources(user: User): List[CompanyResource] = ???

  /**
    * TODO Ex33
    * Get all available resources for employee. Reuse method getAllAvailableResources.
    */
  def getAllAvailableResourcesForEmployee(employee: Employee): List[CompanyResource] = ???

  /**
    * TODO Ex34
    * Return the highest ranking supervisor of employee (manager of manager of manager ..., etc.). If employee has no manager, just return that employee.
    * Try to make method tail-recursive.
    */
  @tailrec
  def getHighestRankingSuperior(employee: Employee): Option[Employee] =
    employee.managerId match {
      case Some(managerId) => CompanyRepository.employees.find(_.id == managerId) match {
        case Some(manager) => getHighestRankingSuperior(manager)
        case _ => None
      }
      case None => Some(employee)
    }

  /**
    * TODO Ex37
    * Implement function for updating employees salaries.
    *
    * Use method actions.calculateRaise to new value of salary for every passed employee. Then send changes to remote
    * server using method batchUpdateEmployeesInRemoteService
    */
  def moveEmployeeToDepartment(employee: Employee, department: Department)(
      implicit ec: ExecutionContext
  ): Future[Unit] = ???

  /**
    * TODO Ex38
    * Implement function for updating employees salaries.
    *
    * Use method actions.calculateRaise to new value of salary for every passed employee. Then send changes to remote
    * server using method batchUpdateEmployeesInRemoteService
    */
  def giveEmployeesRaise(employees: List[Employee])(
      implicit ec: ExecutionContext
  ): Future[Unit] = ???

}
