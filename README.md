# Guice使用入门
Guice是谷歌推出的一个轻量级依赖注入框架，帮助我们解决Java项目中的依赖注入问题。如果使用过Spring的话，会了解到依赖注入是个非常方便的功能。不过假如只想在项目中使用依赖注入，那么引入Spring未免大材小用了。这时候我们可以考虑使用Guice。本文参考了Guice官方文档，详细信息可以直接查看Guice文档。

## 基本使用
如果使用Maven的话，添加下面的依赖项。

```
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>4.2.2</version>
</dependency>
```
## 快速开始
Guice的注入非常方便，不需要配置文件。

```java
// 被依赖的dao
@Singleton // 打上了这个标记说明是单例的，否则Guice每次回返回一个新的对象
public class UserDao{
   public void say(){
        System.out.println("dao is saying");
   }
}

// service，依赖 UserDao
public class UserService {

    @Inject
    private UserDao mUserDao;

    public void say() {
        return mUserDao.say();
    }
}

// 启动类
public class Start  {
   public static void main(final String[] args) {
        //这步就是我们问Guice去要对象
        final Injector injector = Guice.createInjector();
        final UserService userService = injector.getInstance(UserService.class);
        userService.say();
    }
}
```

结果输出：

    dao is saying

复制代码可以看到没有任何的xml配置，唯一需要做的，就是在需要注入的属性上打上`@inject`。
使用 `Guice.createInjector()` 启动。通常需要尽早在程序中创建注入器。这样 Guice 能够帮助您创建大部分对象.

**该demo中，并没有用到Module，也成功运行了，是因为之前没有涉及到接口，当只是依赖<font color = 'red'> 确切的实现类 </font> 的时候，Guice会自动的找到需要注入的实现类**
## 依赖绑定

### 链式绑定
我们在绑定依赖的时候不仅可以将父类和子类绑定，还可以将子类和更具体的子类绑定。下面的例子中，当我们需要`TransactionLog`的时候，`Guice`最后会为我们注入`MySqlDatabaseTransactionLog`对象。

```java
public class BillingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TransactionLog.class).to(DatabaseTransactionLog.class);
        bind(DatabaseTransactionLog.class).to(MySqlDatabaseTransactionLog.class);
    }
}
```

### 注解绑定
当我们需要将多个同一类型的对象注入不同对象的时候，就需要使用注解区分这些依赖了。最简单的办法就是使用@Named注解进行区分。

首先需要在要注入的地方添加@Named注解。

```java
public class RealBillingService implements BillingService {

  @Inject
  public RealBillingService(@Named("Checkout") CreditCardProcessor processor,
      TransactionLog transactionLog) {
    ...
  }
```

然后在绑定中添加`annotatedWith`方法指定`@Named中`指定的名称。由于编译器无法检查字符串，所以Guice官方建议我们保守地使用这种方式。

```java
bind(CreditCardProcessor.class)
    .annotatedWith(Names.named("Checkout"))
    .to(CheckoutCreditCardProcessor.class);
```

如果希望使用**类型安全**的方式，可以自定义注解。

```java
@BindingAnnotation 
@Target({ FIELD, PARAMETER, METHOD }) 
@Retention(RUNTIME)
public @interface PayPal {
}
```
然后在需要注入的类上应用。

```java
public class RealBillingService implements BillingService {

  @Inject
  public RealBillingService(@PayPal CreditCardProcessor processor,
      TransactionLog transactionLog) {
    ...
  }
```

在配置类中，使用方法也和@Named类似。

```java
bind(CreditCardProcessor.class)
    .annotatedWith(PayPal.class)
    .to(PayPalCreditCardProcessor.class);
```

### 实例绑定
有时候需要直接注入一个对象的实例，而不是从依赖关系中解析。如果我们要注入基本类型的话只能这么做。

```java
bind(String.class)
    .annotatedWith(Names.named("JDBC URL"))
    .toInstance("jdbc:mysql://localhost/pizza");
    
bind(Integer.class)
    .annotatedWith(Names.named("login timeout seconds"))
    .toInstance(10);
```

如果使用`toInstance()`方法注入的实例比较复杂的话，可能会影响程序启动。这时候可以使用`@Provides`方法代替。


### @Provides方法
当一个对象很复杂，无法使用简单的构造器来生成的时候，我们可以使用`@Provides`方法，也就是在配置类中生成一个注解了`@Provides`的方法。在该方法中我们可以编写任意代码来构造对象。

```java
public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
    ...
  }

  @Provides
  TransactionLog provideTransactionLog() {
    DatabaseTransactionLog transactionLog = new DatabaseTransactionLog();
    transactionLog.setJdbcUrl("jdbc:mysql://localhost/pizza");
    transactionLog.setThreadPoolSize(30);
    return transactionLog;
  }
}
```

    `@Provides`方法也可以应用`@Named`和自定义注解，还可以注入其他依赖，Guice会在调用方法之前注入需要的对象。

```java
  @Provides @PayPal
  CreditCardProcessor providePayPalCreditCardProcessor(@Named("PayPal API key") String apiKey) {
    PayPalCreditCardProcessor processor = new PayPalCreditCardProcessor();
    processor.setApiKey(apiKey);
    return processor;
  }
```

### Provider绑定
如果项目中存在多个比较复杂的对象需要构建，使用`@Provides`方法会让配置类变得比较乱。我们可以使用Guice提供的`Provider`接口将复杂的代码放到单独的类中。办法很简单，实现`Provider<T>`接口的`get`方法即可。在`Provider`类中，我们可以使用`@Inject`任意注入对象。

```java
public class DatabaseTransactionLogProvider implements Provider<TransactionLog> {
  private final Connection connection;

  @Inject
  public DatabaseTransactionLogProvider(Connection connection) {
    this.connection = connection;
  }

  public TransactionLog get() {
    DatabaseTransactionLog transactionLog = new DatabaseTransactionLog();
    transactionLog.setConnection(connection);
    return transactionLog;
  }
}
```

在配置类中使用`toProvider`方法绑定到`Provider`上即可。

```java
public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TransactionLog.class)
        .toProvider(DatabaseTransactionLogProvider.class);
  }
```

## 作用域
默认情况下Guice会在每次注入的时候创建一个新对象。如果希望创建一个单例依赖的话，可以在实现类上应用`@Singleton`注解。

```java
@Singleton
public class InMemoryTransactionLog implements TransactionLog {
  /* everything here should be threadsafe! */
}
```

或者也可以在配置类中指定。

```java
bind(TransactionLog.class)
    .to(InMemoryTransactionLog.class)
    .in(Singleton.class);
```

在`@Provides`方法中也可以指定单例。

```java
@Provides 
@Singleton
TransactionLog provideTransactionLog() {
    ...
}
```

如果一个类型上存在多个冲突的作用域，`Guice`会使用`bind()`方法中指定的作用域。如果不想使用注解的作用域，可以在`bind()`方法中将对象绑定为`Scopes.NO_SCOPE`。

Guice和它的扩展提供了很多作用域，有单例`Singleton`，Session作用域`SessionScoped`，Request请求作用域`RequestScoped`等等。我们可以根据需要选择合适的作用域。



参考：
[Google-Guice入门教程](https://juejin.im/post/5a375e156fb9a0452a3c6b96)

[Guice 快速入门](https://www.jianshu.com/p/a648322dc680)