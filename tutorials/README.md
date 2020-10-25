# Tutorials 

## General notes

All tutorials focus on the usage of Jexxa. Therefore, the business logic in these tutorials is without any special meaning. In addition, we assume that you have a basic understanding of: 
*   Writing Java code and build your programs using maven. 
*   A general understanding of the used technology stacks such as a messaging system or a database.
*   A general understanding of [ports and adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/).
*   All tutorials run by default without any additional infrastructure services such as JMS or a database. In case you want to run the tutorials with your infrastructure, you should first run the steps `Build Jexxa from scratch` in Jexxa‘s [README.md](../README.md).    
*   All tutorials use the latest public release of Jexxa and not the SNAPSHOT version of the current trunk. In case you want to make some changes in Jexxa itself replace `${jexxa-core.version}` with `${project.version}` in [parent-pom](pom.xml).    

## HelloJexxa
See documentation [HelloJexxa](HelloJexxa/)

## TimeService
See documentation [TimeService](TimeService/)

## BookStore
See documentation [BookStore](BookStore/)

## BookStoreJ
See documentation [BookStoreJ](BookStoreJ/)

## BookStoreJ - With OpenAPI support 
See documentation [BookStoreJ](BookStoreJ/README-OPENAPI.md)
