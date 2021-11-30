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
  def employeesEmails: List[String] = ???

  /**
    * TODO Ex3
    * Implement function for getting tuples of full name and email of all employees. Use method fullName from Employee class.
    */
  def employeesNamesWithEmails: List[(String, String)] = ???

  /**
    * TODO Ex4
    * Implement function returning list of tuples containing list of employee names (fullName) and phone for every phone of each employee.
    *
    * HINT: use flatMap or flatten
    */
  def getAllPhones: List[(String, String)] = ???

  /**
    * TODO Ex5
    * Return list of string containing first and last name of employee sorted alphabetically by the last name.
    */
  def sortedEmployeesNames: List[String] = ???

  /**
    * TODO Ex6
    * Return list of employees as string in following format:
    * 1. John Steward
    * 2. Jane Doe
    * ....
    *
    * HINT: Use zipWithIndex and mkString
    */
  def listOfEmployeesNames: String = ???

  /**
    * TODO Ex7
    * Return list of employees as string in following format:
    * A. John Steward
    * B. Jane Doe
    * ....
    *
    * HINT: Use range, zip and mkString
    */
  def listOfEmployeesNamesWithLetters: String = ???

  /**
    * TODO Ex8b
    * Implement function to get maximum and minimum value of salary and return it as an option of tuple (minimum, maximum).
    * Return None is passed list is empty
    */
  def minAndMaxSalary(employees: List[Employee]): Option[(Salary, Salary)] = ???

  /**
    * TODO Ex9
    * Implement function to modify salaries in the list by multiplying value by modifier. Apply modifier1 for lowest 2, and modifier2 for the rest.
    */
  def modifySalaries(salaries: List[Salary], modifier1: Double, modifier2: Double): List[Salary] = {

    def modify(v: Double)(salary: Salary): Salary = salary.copy(value = (salary.value * v).toInt)

    val sorted = salaries.sorted

    sorted.dropRight(2).map(modify(modifier2)) ++ sorted.takeRight(2).map(modify(modifier1))
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
  def getAllActiveEmployees: List[Employee] = ???

  /**
    * TODO Ex16
    * Implement function for getting tuples of employees email and employee instance by domain passed as argument.
    */
  def findUsersByDomain(domain: String): List[(String, Employee)] = ???

  /**
    * TODO Ex17b
    * Implement function that return sum of salaries of all top level managers
    */

  def getSumOfTopLevelManagersSalaries(): Int = ???

  /**
    * TODO Ex18
    * Implement function that sends a mail to all employees with departmentId passed as argument.
    *
    * HINT: Use foreach
    */

  def sendMailToAllEmployeesOfDepartment(departmentId: Int, message: String): Unit = ???

  /**
    * TODO Ex19
    * Split employees and return tuple containing 3 lists of employees:
    *  1st list of employees having more than 1 phone
    *  2nd list of employees having more than 1 employment period
    *  3rd list for the rest
    *
    *  HINT: Use foldLeft
    */
  def splitEmployees: (List[Employee], List[Employee], List[Employee]) = ???

  /**
    * TODO Ex20
    * Implement function returning office location of employee as "<building>: <office>" but returning Some only
    * if employee has office.
    * HINT: use flatMap
    */
  def getOfficeOfEmployee(e: Employee): Option[String] = ???

  /**
    * TODO Ex22
    * Implement function that returns all possible pairs of employees names passed as argument.
    * For example for input:
    * Rick, Brooke, Terry
    * return
    * (Rick,Brooke), (Rick,Terry), (Brooke,Rick), (Brooke,Terry), (Terry,Rick), (Terry,Brooke)
    * HINT: use flatMap
    */
  def getPairsOfEmployees(employeeNames: List[String]): List[(String, String)] = for {
    e1 <- employeeNames
    e2 <- employeeNames if e1 != e2
  } yield (e1, e2)

  /**
    * TODO Ex21
    * Implement ordering for localData
    */
  implicit val ordering: Ordering[LocalDate] = ???

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
  def getHighestRankingSuperior(employee: Employee): Option[Employee] = ???

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
