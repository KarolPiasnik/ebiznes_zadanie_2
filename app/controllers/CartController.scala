package controllers

import models.Cart
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}

import javax.inject.Inject
import javax.inject.Singleton
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class CartController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  private val carts = new mutable.ListBuffer[Cart]()
  carts += Cart(1)
  carts += Cart(2)
  implicit val cartList = Json.format[Cart]

  def getAll(): Action[AnyContent] = Action {
    if (carts.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(carts))
    }
  }

  def getById(id: Long) = Action {
    val foundItem = carts.find(_.id == id)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def create() = Action.async {
    request => {
      val cart = request.body.asJson.get.as[Cart]
      carts.addOne(cart)
      val jsonValue = Json.toJson(cart)
      Future {
        Ok(jsonValue + " object created")
      }
    }
  }

  def update() = Action.async {
    request => {
      val updatedCart = request.body.asJson.get.as[Cart]
      val index = carts.map(p => p.id).indexOf(updatedCart.id)
      carts.update(index, updatedCart)
      val jsonValue = Json.toJson(updatedCart)
      Future {
        Ok(jsonValue + " object updated")
      }
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    val index = carts.map(p => p.id).indexOf(id)
    val cart = carts.apply(index)
    carts.remove(index)
    val jsonValue = Json.toJson(cart)
    Future {
      Ok(jsonValue + " object deleted")
    }
  }
}