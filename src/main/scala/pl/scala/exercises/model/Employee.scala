package pl.scala.exercises.model

import pl.scala.exercises.CompanyRepository

import java.time.LocalDate
import scala.language.postfixOps
import scala.util.Try

case class Location(building: String, office: Option[String])
case class Department(id: Int, name: String)
case class EmploymentPeriod(from: LocalDate, to: Option[LocalDate], departmentId: Int)

object EmploymentPeriod {

  /**
    * TODO Ex23
    * Implement function that checks if there are any overlapping employment periods.
    * HINT: use grouped
    */
  def checkIfPeriodsOverlap(periods: Vector[EmploymentPeriod]): Boolean = ???

}

case class Employee(
    id: Int,
    firstName: String,
    lastName: String,
    email: String,
    employmentHistory: Vector[EmploymentPeriod],
    phones: List[String],
    salary: Salary,
    managerId: Option[Int] = None,
    location: Option[Location] = None
) {

  /**
    * TODO Ex2
    * Implement value returning fullName of employee (firstName + lastName)
    */
//  val fullName: String = this.firstName + " " + this.lastName
  val fullName: String = s"$firstName $lastName"

  /**
    * TODO Ex12
    * Implement function that returns true, if employee has employment period for given day.
    */
  def isActiveAt(day: LocalDate): Boolean =
//    employmentHistory.exists{
//      case EmploymentPeriod(from, maybeTo, _ ) =>
//        (from.isBefore(day)|| from.isEqual(day)) &&
//          maybeTo.map(to => to.isEqual(day) || to.isAfter(day)).getOrElse(true)
//    }     PIERWSZY SPOSOB

    employmentHistory.exists{
      case EmploymentPeriod(from, Some(to), _ ) =>
        (from.isBefore(day)|| from.isEqual(day)) &&
          to.isEqual(day) || to.isAfter(day)
      case EmploymentPeriod(from, None, _) =>
        from.isBefore(day) || from.isEqual(day)
    }

//    employmentHistory.forall{
//      case EmploymentPeriod(from, Some(to), _) =>
//        day.isEqual(from) || (day.isAfter(from) && to.isBefore(day))
//      case EmploymentPeriod(from, None, _) =>
//        day.isAfter(from)


  /**
    * TODO Ex14
    * Implement function returning current active department for employee.
    */
  def getActiveDepartment: Option[Department] = {
//    val depId : Int = employmentHistory.filter(x => x.from.isBefore(LocalDate.now()) || x.from.isEqual(LocalDate.now())).last.departmentId
//    Some(CompanyRepository.departments.filter(_.id == depId))     PIERWSZA OPCJA NIE DO KONCA

//    if (isActiveAt(LocalDate.now())) {
//      val depId = employmentHistory.sortBy(_.from).last.departmentId
//      CompanyRepository.departments.find(_.id == depId)
//    } else {
//      None
//    }   DRUGA OPCJA

    val day = LocalDate.now();
    val maybeActivePeriod = employmentHistory.find{
      case EmploymentPeriod(from, Some(to), departmentId) =>
        (from.isBefore(day) || from.isEqual(day)) &&
          (to.isEqual(day) || to.isAfter(day))
      case EmploymentPeriod(from, None, _) =>
        from.isBefore(day) || from.isEqual(day)
    }

    maybeActivePeriod.flatMap(ep => CompanyRepository.departments.find(_.id == ep.departmentId))
  }

  /**
    * TODO Ex35
    * Implement function that updates last employment period of employee and sets the "to" date to value passed as argument.
    * Update "to" value only if it's None. If it's Some leave it unchanged.
    * If employee has no employment periods return Failure with IllegalStateException having error message: "No periods!"
    *
    * If validation is successful return Success with new instance of Employee.
    */
  def endLastEmploymentPeriod(endDate: LocalDate): Try[Employee] = ???

  /**
    * TODO Ex36
    * Implement function creating a new instance of Employee with new employment period added. New employment period can't
    * overlap already existing periods. In that case return Failure with IllegalStateException having error message: "Overlapping periods!".
    * Additionally there can be only 1 period with unspecified end date. In case such period already exists return Failure with
    * IllegalStateException with message "Only 1 opened period allowed!"
    *
    * If validation is successful return Success with new instance of Employee.
    */
  def withNewEmploymentPeriod(from: LocalDate, department: Department): Try[Employee] = ???

}

object Employee {

  private val CompanyDomain = "acme.com"
  private val InitialSalary = Salary(2000, "USD")

  /**
    * TODO Ex10
    * Implement apply method that returns instance of Either[String, Employee]. In case managerId is equal to employee's id return Left
    * with string "Manager id should be different that employee's id". Use InitialSalary for salary. Use method createEmail to create email
    */
  def apply(id: Int, firstName: String, lastName: String, phone: String, managerId: Option[Int]): Either[String, Employee] = {
    def createEmail = s"$firstName.$lastName@$CompanyDomain"

    if (managerId.contains(id)) {
      Left("Manager id should be different that employee's id")
    } else {
      Right(new Employee(id, firstName, lastName, createEmail, Vector.empty, List(phone), InitialSalary, managerId = managerId, location = None))
    }
  }
}

/**
  * TODO Ex15
  * Implement `unapply` method that allows matching by email's domain. It should match (domain, whole email).
  */
object EmailDomain {

  def unapply(employee: Employee): Option[(String, String)] =
    employee.email.split('@') match {
      case Array(_, domain) => Some((domain, employee.email))
      case _ => None
    }
}

/**
  * TODO Ex17a
  * Implement unapply function that matches only top-level managers (with empty managerId).
  */
object TopLevelManager {
  def unapply(e: Employee): Option[Employee] =
//    e.managerId.fold(Option(e))(_ => None)      pierwsze rozwiazanie
//  if(e.managerId.isEmpty) Some(e) else None     ten fold robi to samo co ten if
  e.managerId match {
    case Some(_) => None
    case None => Some(e)
  }
}
