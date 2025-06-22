что сделано

Лекция 1. 03.03.2025 -- 10.03.2025

Теория
    ```
    + база по Maven (структура файла, описание её элементов, база о жизненном цикле)
    + JavaBeans (то, что это "стандарт")
    + Learn TDD
    + boundary testing
    + code coverage
    + Learn junit4
    + DAO-Concept
    +- Read carefully Chapters 2 (The servlet instance)
    + Learn mockito (по крайне мере мне стало куда понятнее Как им пользоваться)
    ```
Практика
Task 1.5
    ```
    + Implement ArrayListProductDao (all methods).
    + ArrayListProductDao shall be thread safe.
    + The method shall return products having non null price and stock level > 0.
    + Use java8 streams to do the filtering.
    + Implement ArrayListProductDaoTest using junit4
    ```
Task 1.6
    ```
    + Inject ArrayListProductDao into ProductListPageServlet during servlet initialization.
    + Implement ProductListServletTest using junit4 and mockito
    ```
Homework1
    ```
    + Set up remote debugging via Intellij
    + Implement ArrayListProductDaoTest using Mockito Too (у меня там нет зависимостей таких, чтобы их мокать
                                                            поэтому думаю что не нужно и противоречие между
                                                            заданиями решимо)                                                      
    ```

Лекция 2. 10.03.2025 -- 17.03.2025
Теория
    ```
    + HTTP GET method
    + Read carefully Chapters 4, 11 (Servlet Context, Application Lifecycle Events) of the servlet-api spec
    + Read carefully Chapter 9 (Dispatching Requests) of the servlet-api spec
    + Learn java singleton
    ```
Практика
{
    ```
    Task 2.1
    + The search must work using OR clause.
        The example above means: search all products having a description containing “Samsung” or “S” or “II”. 
        Products having all search terms must come first in the search result.
            Use java8 streams.
    ```
    ```
    Task 2.2
    + It should be possible to sort PLP on product description or price.
    + By default there is no sorting.
            Use java8 streams.
    ```
}
    ```
    Task 2.3 
    + Implement ProductDemodataServletContextListener to load demodata into ProductDao.
    + It should be possible to enable/disable the listener via the config parameter in web.xml.
    ```
    ```
    Task 2.4
    + Make ArrayListProductDao a singleton. 
        + The singleton must be thread safe.    
    + Implement ProductDetailsPageServlet.doGet() to load product details. 
    + Bind servlet to /products/* URL where * is any product code.
    + Add a new page productDetails.jsp to render product details.
    + The PDP and PLP shall share a common header / footer with PLP using JSP 2.0 tags.
    + Display 404 page with “Product with code xxx not found” message if a product can’t be found.
    ```
    ```
    Task 2.5
    + Clicking product price shall open a popup with price change history. (1:56:00)
    ```    
Homework2
    ```
    + исправить тесты
    + Implement unit tests
    ```
По возможности ("моё желание")
    ```    
    - разделить тесты
    ```

Лекция 3. 17.03.2025 -- 24.03.2025

Теория
    ```
    + HTTP methods
    +- Read carefully Chapters 5
    - Read carefully Chapter 7
    ```

Практика
    ```
    Task 3.1: CartService
    + Implement Cart, CartItem, and dead simple CartService.add(Long productId, Int quantity) storing cart in a field.
    ```
    ```
    Task 3.2: PDP add2cart
    + Implement ProductDetailsPageServlet.doPost() to add product to cart.
    + Display “Not a number” error message if a quantity can’t be parsed.
    + Display “Not enough stock” error message if there is not enough stock.
    + Display “Added to cart successfully” message if the product was added to cart.
        + Redirect to /products/* Pass success message via HTTP parameter to survive redirect.
    todo : удостовериться + Use browser locale to parse / format quantity.
    ```
    ```
    Task 3.3: HttpSessionCartService
    + Refactor CartService to store cart in HTTP session.
    ```
    ```
    Task 3.4 [selfcheck]: Implement 3 recently viewed products
    + Implement a new section showing 3 recently viewed products.
        + сделать tag
    + The section shall be displayed at the bottom of the site.
    + “Recently viewed” means the product was opened on PDP.
    + The content of the section shall be customer specific.
        + хранить в session (по аналогии с Cart)
    ```
Homework3

По возможности ("моё желание")
    ```
    + избавиться от Cart getCart(HttpServletRequest request);
    ```

Лекция 4. 25.03.2025 -- 01.04.2025
notes/lecture4/notes.txt

Теория
Практика

Task 4.1: show cart
+ Implement CartPageServlet.doGet() and cart.jsp to show empty cart page.
+ Bind the servlet to /cart URL.
+ Make Cart class Serializable to facilitate server restart.

Task 4.2: Cart update
+ Implement cart update via CartPageServlet.doPost().
+ The whole cart shall be updated via single button.

Task 4.3: Delete cart item
+ Implement CartItemDeleteServlet.
+ Bind it to POST:/cart/deleteCartItem
    Possible options:
        DELETE: /cart/<productId>
        => POST: /cart/deleteCartItem/<productId>
        POST: /cart/deleteCartItem?productId=<productId>

Task 4.4: MiniCart
+ Display minicart on PLP and PDP.
+ Think about technical approach: filter or include another servlet.
+ Implement MiniCartServlet.doGet().
    Bind it to GET:/cart/minicart

Task 4.5: [selfcheck] Implement PLP add to cart
+ Implement add to cart from search result page.


Homework4
+ общее количество и стоимось в cart.jsp

подразумевается, но не упоминается
    с прошлого раза:
        + тестирование Servlet-ов
        + тестирование Reader-ов
        + перезагрузка при переходе обратно


Лекция 5. 02.04.2025 -- 09.04.2025

Task 5.1: Checkout page
+ Implement CheckoutPageServlet.
    Bind the servlet to /checkout URL
+ The page should provide an information table without editing capabilities:
    + cart overview
    + cart subtotal
    + delivery costs (hardcoded)
    + cart total (cart subtotal + delivery costs)
+ Should be possible to enter:
        + Contact details (First name, Last name, phone)
        + Delivery date
        + Delivery address
        + Payment method selectbox: cash, credit card
        + Place order button
- Implement OrderDao, OrderService.placeOrder().

Task 5.2: OrderOverview (OrderComfirmation) page
+ Implement OrderOverviewPageServlet.
    URL: /order/overview/*
+ Implement secure orderId generation.

Task 5.3: primitive DOS protection
+ Implement DosFilter which blocks an IP if amount of requests from it
    + per minute exceeds 20.

Task 5.4: Deploy to standalone tomcat
+ Build war using “mvn package” goal.
+ Download tomcat zip from from http://tomcat.apache.org/
+ Deploy the war to the tomcat on local environment.
+ Make the application available by url: http://servlet-api-ecommerce.com/
    Implementation hints:
        Use hosts file to map servlet-api-ecommerce domain to localhost
        The application must be available under / context path.T

Homework
- Prepare for online test / coding task
