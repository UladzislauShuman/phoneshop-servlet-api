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