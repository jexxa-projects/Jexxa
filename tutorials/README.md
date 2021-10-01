# Tutorials 

## General notes

All tutorials focus on the usage of Jexxa. Therefore, the business logic in these tutorials is without any special meaning. In addition, we assume that you have a basic understanding of: 
* Writing Java code and build your programs using maven. 

* A general understanding of the used technology stacks such as a messaging system or a database.

* A general understanding of [ports and adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/).

* All tutorials run by default without any additional infrastructure services such as JMS or a database. In case you want to run the tutorials with your infrastructure:
    * You should first run the steps `Build Jexxa from scratch` in Jexxa‘s [README.md](../README.md) to validate your infrastructure. 
    * If you do not have this infrastructure running on your developer machine, you can use docker together with the docker stack provided [here](https://github.com/repplix/Jexxa/blob/master/jexxa-core/src/test/resources/DeveloperStack.yaml).
  
* All tutorials use the latest public release of Jexxa and not the SNAPSHOT version of the current trunk. In case you want to make some changes in Jexxa itself replace `${jexxa-core.version}` with `${project.version}` in [parent-pom](pom.xml).    

## HelloJexxa
See documentation [HelloJexxa](HelloJexxa/)

## TimeService - Async Messaging
See documentation [TimeService](TimeService/)

## TimeService - Flow of Control
See documentation [TimeService - Flow of Control](TimeService/README-FlowOfControl.md)

## BookStore - Using a Repository  
See documentation [BookStore](BookStore/)

## BookStoreJ - Pattern Language 
See documentation [BookStoreJ](BookStoreJ/)

## BookStoreJ - OpenAPI Support 
See documentation [BookStoreJ - With OpenAPI support](BookStoreJ/README-OPENAPI.md)

## BookStoreJ16 - Java Records
See documentation [BookStoreJ16](BookStoreJ16/)

## ContractManagement - Using an ObjectStore  
See documentation [ContractManagement](ContractManagement/) 