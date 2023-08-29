package controllers

import models.Product
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}

import javax.inject.Inject
import javax.inject.Singleton
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class ProductController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  private val products = new mutable.ListBuffer[Product]()
  products += Product(1, "guma turbo", 2.22, 1)
  products += Product(2, "guma donald", 3.33, 1)
  implicit val productList = Json.format[Product]

  def getAll(): Action[AnyContent] = Action {
    if (products.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(products))
    }
  }

  def getById(id: Long) = Action {
    val foundItem = products.find(_.id == id)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def create() = Action.async {
    request => {
      val product = request.body.asJson.get.as[Product]
      products.addOne(product)
      val jsonValue = Json.toJson(product)
      Future {
        Ok(jsonValue + " object created")
      }
    }
  }

  def update() = Action.async {
    request => {
      val updatedProduct = request.body.asJson.get.as[Product]
      val index = products.map(p => p.id).indexOf(updatedProduct.id)
      products.update(index, updatedProduct)
      val jsonValue = Json.toJson(updatedProduct)
      Future {
        Ok(jsonValue + " object updated")
      }
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    val index = products.map(p => p.id).indexOf(id)
    val product = products.apply(index)
    products.remove(index)
    val jsonValue = Json.toJson(product)
    Future {
      Ok(jsonValue + " object deleted")
    }
  }
}