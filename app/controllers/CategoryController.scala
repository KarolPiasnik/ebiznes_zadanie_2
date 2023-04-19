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

  private val categorys = new mutable.ListBuffer[Category]()
  categorys += Category(1, "gumy", "różne gumy")
  categorys += Category(2, "cukierki", "coś słodkiego")
  implicit val todoListJson = Json.format[Category]

  def getAll(): Action[AnyContent] = Action {
    if (categorys.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(categorys))
    }
  }

  def getById(id: Long) = Action {
    val foundItem = categorys.find(_.id == id)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def create() = Action.async {
    request => {
      val category = request.body.asJson.get.as[Category]
      categorys.addOne(category)
      val jsonValue = Json.toJson(category)
      Future {
        Ok(jsonValue + " object created")
      }
    }
  }

  def update() = Action.async {
    request => {
      val updatedCategory = request.body.asJson.get.as[Category]
      val index = categorys.map(p => p.id).indexOf(updatedCategory.id)
      categorys.update(index, updatedCategory)
      val jsonValue = Json.toJson(updatedCategory)
      Future {
        Ok(jsonValue + " object updated")
      }
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    val index = categorys.map(p => p.id).indexOf(id)
    val category = categorys.apply(index)
    categorys.remove(index)
    val jsonValue = Json.toJson(category)
    Future {
      Ok(jsonValue + " object deleted")
    }
  }
}