# Entity Assist
The ultimate Criteria SQL Generator utlizing CRP, AOP and designed in a complete DDD environment.

Utilizing the Metamodel, and forcing the builder to be strictly tied to the entity, you can wrap your common statements in easy-to-use methods.

Artifactory : http://www.jwebswing.com/artifactory/

Direct Link : https://jwebswing.com/artifactory/list/libs-snapshot-local/za/co/mmagon/entity-assist/

# Getting Started
```

    <repositories>
        <repository>
            <id>jwebswing-snapshot</id>
            <name>JWebSwingArtifactory-snapshots</name>
            <url>https://jwebswing.com/artifactory/libs-snapshot</url>
        </repository>
    </repositories>
    
    <dependency>
        <groupId>com.jwebmp</groupId>
        <artifactId>entity-assist</artifactId>
        <version>[0.32.0.1,)</version>
    </dependency>

```
*Deployment to maven central scheduled for April


# Creating your queries
Standard operation allows for simplistic query writing.

The return methods are get(), and getAll() where get() will return an optional of a single result, and getAll() will return all results) 

```
List<EntityClass> list = new EntityClass().builder()
				                         .inDateRange()
				                         .join(EntityClassTwo_.entityClass)
				                         .getAll();
				                         
builder().where(Visitors_.localStorageKey, Operand.Equals,"Value");
		builder().where(Visitors_.localStorageKey, Operand.Like,"Value");
		builder().where(Visitors_.localStorageKey, Operand.NotLike,"Value");
```

By default all entities inherit the builder() method that configures and constructs your entities

# Building queries
The builder method grants access to the entire builder API, 

```
builder()
				.selectAverage(Visitors_.id)
				.selectCount(Visitors_.id)
				.selectMax(Visitors_.id)
				.selectMin(Visitors_.id)
				.selectSum(Visitors_.id)
				.selectSumAsDouble(Visitors_.id)
				.selectSumAsLong(Visitors_.id)
				.selectCountDistinct(Visitors_.id)
				.groupBy(Visitors_.subscribersList)
				.orderBy(Visitors_.id, OrderByType.ASC)
				.construct(Visitors.class)
				.detach()
				.update(new Visitors())
				.noLock()
				.setRunDetached(true)
				.where(Visitors_.localStorageKey,Operand.NotInList,new HashSet<>())
				.where(Visitors_.localStorageKey,Operand.NotInList,new ArrayList<>())
				.where(Visitors_.localStorageKey,Operand.NotInList,new Visitors[0])
```

# Making easy to re-use methods
The builder class can also be used to dynamic query generation to group commonly used expressions into an easy-to-read format

```
Optional<Subscribers> found = new Subscribers().builder.findByEmail("mailaddress@from.com").get();
Optional<Subscribers> authenticated = new Subscribers().builder.findByEmail("mailaddress@from.com").withPassword("encryptedPassword1").get();
Optional<Subscribers> accountConfirmed = new Subscribers().builder.withUnconfirmedKey().get();
```

```

public SubscribersBuilder findByEmail(String email)
{
    where(Subscribers_.emailAddress, Equals, email);
    return this;
}

public SubscribersBuilder findByConfirmationKey(String confirmationKey)
{
    where(Subscribers_.confirmationKey, Equals, confirmationKey);
    return this;
}

public SubscribersBuilder withUnconfirmedKey()
{
    where(Subscribers_.confirmed, Equals, false);
    return this;
}

public SubscribersBuilder findNewSubscribed()
{
    where(Subscribers_.unsubscribed, Equals, false);
    return this;
}

public SubscribersBuilder findNewsUnsubscribed()
{
    where(Subscribers_.unsubscribed, Equals, true);
    return this;
}

public SubscribersBuilder withPassword(String password)
{
    where(Subscribers_.password, Equals, password);
    return this;
}

```


# Configuring An Entity
Entities are configured through type safety and CRP, and are strictly tied to a builder (QueryBuilderExecutor)

```
class Entity<J,Q,I> extends BaseEntity
```
J> is always the type of the type being referenced (CRP)

Q> is the query builder instance that must extend QueryBuilderExecutor

I> is the serializable that identifies they Entity Key. Embedded and Mapped keys are of course allowed

```
class EntityBuilder<J,E,I> extends QueryBuilderExecutor
```
J> is always the type being referred (EntityBuilder in the example)

E> is the entity type that maps properly to the strictly typed class

I> is the key type (Forced type safety between builder and entity

## Example
https://github.com/GedMarc/EntityAssist/tree/master/src/test/java/za/co/mmagon/entityassist/entities

```
public class EntityClass extends BaseEntity<EntityClass, EntityClassBuilder, Long>

public class EntityClassBuilder extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
```


# It has everything
The API is incredibly easy to use, and while we write out the wiki, you are more than free to log issues, make requests and improve on the stability (Although we are already using this in production)

Order By, Group By, Having, Count, Select Columns, Construct, Detach, Insert, Update, Batch Criteria Update Delete and Insert all there ready for you.

# Inherit Dependencies
* Guice 4.1
* GuiceContent - A complete injection management framework for Guice. This is included for cross-compatibility with any Standalone, EE or specific WELD implementation. 
* GuicedPersistence (optional) - A complete JTA1.1 BTM operated management library. Ensure that your unit/integrated test environment matches your production environment 100% with no nasty surprises based on environment or container. 

# Support
Support is offered free of charge, and the library is maintained (although we have pretty much ironed everything out)
* Tested on Wildlyfly >= 10
* Tested on Glassfish >= 4.1
* Tested on WebSphere
* Tested on Standalone
* Tested on Tomcat
* Tested on TomEE
* Tested on JBoss
* Tested on Payara
* Tested on Payara MP >= 1.0
* Tested on Wildfly Swarm MP
* Jetty/Catalina
