что сделано

Лекция 1. 03.03.2025 -- 10.03.2025

Теория
    + база по Maven (структура файла, описание её элементов, база о жизненном цикле)
    + JavaBeans (то, что это "стандарт")
    + Learn TDD
    + boundary testing
    + code coverage
    + Learn junit4
    + DAO-Concept
    +- Read carefully Chapters 2 (The servlet instance)
    +- Learn mockito

Практика
Task 1.5
    + Implement ArrayListProductDao (all methods).
    + ArrayListProductDao shall be thread safe.
    + The method shall return products having non null price and stock level > 0.
    + Use java8 streams to do the filtering.
    + Implement ArrayListProductDaoTest using junit4

Task 1.6
    + Inject ArrayListProductDao into ProductListPageServlet during servlet initialization.
    + Implement ProductListServletTest using junit4 and mockito

Homework1
    + Set up remote debugging via Intellij
    + Implement ArrayListProductDaoTest using Mockito Too (у меня там нет зависимостей таких, чтобы их мокать
                                                            поэтому думаю что не нужно и противоречие между
                                                            заданиями решимо)                                                      
    