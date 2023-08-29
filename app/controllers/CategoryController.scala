package controllers

import models.Category
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}

import javax.inject.Inject
import javax.inject.Singleton
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class CategoryController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  private val categories = new mutable.ListBuffer[Category]()
  categories += Category(1, "gumy", "różne gumy")
  categories += Category(2, "cukierki", "coś słodkiego")
  implicit val categoryList = Json.format[Category]

  def getAll(): Action[AnyContent] = Action {
    if (categories.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(categories))
    }
  }

  def getById(id: Long) = Action {
    val foundItem = categories.find(_.id == id)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def create() = Action.async {
    request => {
      val category = request.body.asJson.get.as[Category]
      categories.addOne(category)
      val jsonValue = Json.toJson(category)
      Future {
        Ok(jsonValue + " object created")
      }
    }
  }

  def update() = Action.async {
    request => {
      val updatedCategory = request.body.asJson.get.as[Category]
      val index = categories.map(p => p.id).indexOf(updatedCategory.id)
      categories.update(index, updatedCategory)
      val jsonValue = Json.toJson(updatedCategory)
      Future {
        Ok(jsonValue + " object updated")
      }
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    val index = categories.map(p => p.id).indexOf(id)
    val category = categories.apply(index)
    categories.remove(index)
    val jsonValue = Json.toJson(category)
    Future {
      Ok(jsonValue + " object deleted")
    }
  }
}