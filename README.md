@[toc]
# 1 概述
Logback是在java社区广泛使用的日志框架之一，它是Log4j的替代品。Logback提供了比Log4j更快的实现，更多可选择的配置，以及归档log文件更大的灵活性。
# 2 Logback的架构
包括三个类：**Logger，Appender和Layout**。
- Logger， 是log信息的上下文，是跟应用交互以生成log信息之用；
- Appender，负责日志输出的组件；
- Layout，用来格式化日志信息等。
# 3 例子
这里创建一个springboot工程，添加以下依赖
## 3.1 Maven依赖

```xml
	<dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.30</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-core</artifactId>
        <version>1.2.3</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
    </dependency>
```
## 3.2 基本的配置和代码demo
在resources下面创建logback.xml文件，添加以下配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```
创建一个Example：

```java
@Slf4j
public class Example {
    public static void main(String[] args) {
        log.info("Example log from {}", Example.class.getSimpleName());
    }
}

```
运行得到以下结果：
09:59:36.226 [main] INFO  com.logback.demo.Example - Example log from Example。
# 4 Logger context
@Slf4j是来自lombok的注解，相当于如下代码：

```java
private static final Logger log = LoggerFactory.getLogger(Example.class);
```
可以从编译后的class文件，反编译后看到。
## 4.1 logger继承结构
Logger context的继承类似于java的继承。
- 所有的logger都是root logger的后代；
- 如果一个logger没有指定level，那么它的level将继承上一个祖先logger.
- root logger的默认level是DEBUG。

注意：日志级别分为5种，从小到大依次是TRACE, DEBUG, INFO, WARN 和ERROR。低于指定级别的日志将不打印，例如我们指定日志级别是INFO，那么低于INFO的TRACE和DEBUG都不打印。

上代码：

```java
public class LoggerHierarchyExample {

    @Test
    public void testLoggerHierarchy() {
        // 祖先logger，未指定级别，它将继承root logger的级别，root logger默认debug
        // SLF4J's的抽象logger是没有实现setLevel方法，这里是用ch.qos.logback.classic.Logger
        Logger ancestorLogger = (Logger) LoggerFactory.getLogger("com");
        // 父logger，指定级别为info，不继承祖先logger的级别
        Logger parentLogger = (Logger) LoggerFactory.getLogger("com.logback");
        parentLogger.setLevel(Level.INFO);
        // 子logger，未指定级别，继承最近的父logger的日志级别，为info。
        Logger childLogger = (Logger) LoggerFactory.getLogger("com.logback.demo");

        ancestorLogger.debug("This message is logged because debug == debug");
        ancestorLogger.trace("This message is not logged because trace < debug");
        parentLogger.warn("This message is logged because WARN > INFO.");
        parentLogger.debug("This message is not logged because DEBUG < INFO.");
        childLogger.info("INFO == INFO");
        childLogger.debug("DEBUG < INFO");
        
    }


    @Test
    public void testRootLogger() {
        // 没有指定level，继承root logger的level为debug
        Logger logger = (Logger) LoggerFactory.getLogger("com.logback");
        logger.debug("Hi there!");

        // root logger,重新设定level为error
        Logger rootLogger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        logger.debug("This message is logged because DEBUG == DEBUG.");
        rootLogger.setLevel(Level.ERROR);

        logger.warn("This message is not logged because WARN < ERROR.");
        logger.error("This is logged.");
    }
}
```
testLoggerHierarchy()结果为：

```bash
10:49:16.713 [main] DEBUG com - This message is logged because debug == debug
10:49:16.715 [main] WARN  com.logback - This message is logged because WARN > INFO.
10:49:16.715 [main] INFO  com.logback.demo - INFO == INFO
```
testRootLogger() 结果为：

```bash
10:50:06.087 [main] DEBUG com.logback - Hi there!
10:50:06.089 [main] DEBUG com.logback - This message is logged because DEBUG == DEBUG.
10:50:06.089 [main] ERROR com.logback - This is logged.
```
## 4.2 参数化打印日志
实际编码中，看到很多如下日志打印方式：

```java
log.debug("Current count is " + count);
```
这种方式的弊端：即使日志打印level设定为info，都会执行字符串的拼接，无疑会白白的损耗性能。

以下方式也不可取，虽然不执行拼接，但仍然做了一次判断。
```java
if(log.isDebugEnabled()) { 
  log.debug("Current count is " + count);
}
```
更好的方式是**使用占位符的方式** , 避免字符串拼接，避免日志level的判断。
```java
log.debug("Current count is {}" , count);
```
占位符{}允许接收任何object，并且调用其toString方法来记录日志。

```java
@Slf4j
public class ParameterizedExample {

    @Test
    public void testParameterizedExample() {
        String message = "This is a String";
        Integer zero = 0;
        User user = new User().setId("123").setName("Tim");
        try {
            log.debug("Logging message: {}", message);
            log.debug("Going to divide {} by {}", 42, zero);
            log.debug("user is {}", user);
            int result = 42 / zero;
        } catch (Exception e) {
            log.error("Error dividing {} by {} ", 42, zero, e);
        }
    }
}

```

```java
@Data
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
}

```
# 5 详细配置
logback默认行为：假如它没有找到任何配置文件，它将默认创建一个ConsoleAppender，并且关联到root logger。
## 5.1 logback查找配置文件顺序
- logback按照顺序在classpath中查找配置文件：logback-test.xml, logback.groovy,  logback.xml ；
- 假如上面的file都没有找到，则启动Java的 [ServiceLoader](https://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html) 查找com.qos.logback.classic.spi.Configurator的实现；
- 假如以上都没有，则启动logback默认的行为: 创建一个ConsoleAppender，并且关联到root logger。
## 5.2 基本配置解释
所有的配置都在<configuration>里面。

```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
```
这里配置了一个Appender，类型是ConsoleAppender，名称是STDOUT。

```xml
 <root level="debug">
        <appender-ref ref="STDOUT"/>
    </root>
```
<root>标签是root logger，level是debug，关联到STDOUT的Appender。
## 5.3 解决日志故障配置
logback的配置文件可以很复杂，因此logback提供内置机制来排查故障。为查看logback自身的日志，可以打开debug模式：

```xml
<configuration debug="true">
```
打印出的日志如下所示：
```bash
15:17:24,550 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
15:17:24,550 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
15:17:24,550 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback.xml] at [file:/E:/10_git/LogbackDemo/target/classes/logback.xml]
15:17:24,699 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
15:17:24,703 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [STDOUT]
15:17:24,733 |-INFO in ch.qos.logback.core.joran.action.NestedComplexPropertyIA - Assuming default type [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
15:17:24,775 |-INFO in ch.qos.logback.classic.joran.action.RootLoggerAction - Setting level of ROOT logger to DEBUG
15:17:24,775 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [STDOUT] to Logger[ROOT]
15:17:24,776 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
15:17:24,778 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@5606c0b - Registering current configuration as safe fallback point
15:17:24.782 [main] INFO  com.logback.demo.Example - Example log from Example
Disconnected from the target VM, address: '127.0.0.1:50977', transport: 'socket'

Process finished with exit code 0

```
## 5.4 自动重载配置

```xml
<configuration debug="true" scan="true" scanPeriod="15 seconds">
```
scan=true,表示打开自动扫描配置，默认为false。scanPeriod="15 seconds"表示每15秒扫描一次，也可以设置milliseconds, seconds, minutes, or hour，如果没设置，默认为1分钟。
## 5.5 修改Loggers
我们可以给任何logger配置level，如下配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false" scan="true" scanPeriod="15 seconds">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <logger name="com.logback.other" level="INFO"/>
    <logger name="com.logback.demo.tests" level="WARN"/>
    <root level="debug">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

```java
public class ModifyingLoggersExample {
    @Test
    public void testModify() {
        Logger otherLogger = LoggerFactory.getLogger("com.logback.other");
        Logger demoLogger = LoggerFactory.getLogger("com.logback.demo");
        Logger testsLogger = LoggerFactory.getLogger("com.logback.demo.tests");

        otherLogger.debug("otherLogger is logged debug == debug");
        demoLogger.debug("demoLogger is not logged debug < info");
        demoLogger.info("demoLogger is logged info == info");
        testsLogger.info("testsLogger is not logged warn > info");
        testsLogger.warn("testsLogger is logged warn = warn");
    }
}

```
结果：

```bash
15:52:17.359 [main] DEBUG com.logback.other - otherLogger is logged debug == debug
15:52:17.361 [main] INFO  com.logback.demo - demoLogger is logged info == info
15:52:17.362 [main] WARN  com.logback.demo.tests - testsLogger is logged warn = warn
```
Logger同样能继承root logger的appender-ref。
## 5.6 变量替代

```xml
<property name="LOG_DIR" value="/var/log/application" />
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>${LOG_DIR}/tests.log</file>
    <append>true</append>
    <encoder>
        <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
    </encoder>
</appender>
```
我们声明一个<property> 名称是LOG_DIR, 值是/var/log/application，logback会将它的值注入到${LOG_DIR}。

# 6 Appenders
logback支持不进支持log以文件形式输出，还支持其他的形式。
## 6.1 ConsoleAppender
控制台输出，利用System.out 或 System.err输出。
## 6.2 FileAppender
FileAppender是把log输出到文件。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false" scan="true" scanPeriod="15 seconds">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>tests.log</file>
        <append>true</append>
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.logback.demo" level="INFO"/>
    <logger name="com.logback.demo.tests" level="WARN">
        <appender-ref ref="FILE"/>
    </logger>
    <root level="debug">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```
运行ModifyingLoggersExample的testModify可以看到工程中多了一个文件tests.log. 
我们可以同时在tests.log文件和控制台看到同样的输出信息；
![test](https://img-blog.csdnimg.cn/20200412161921799.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200412162025741.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM5NTMwODIx,size_16,color_FFFFFF,t_70)
这是因为logger继承了来自<root> 的<appender-ref , 如果要阻止，只需如下配置：添加additivity="false"

```xml
<logger name="com.baeldung.logback.tests" level="WARN" additivity="false" > 
    <appender-ref ref="FILE" /> 
</logger> 
```
## 6.3 RollingFileAppender
有时候我们不需要将log信息一直输出到同个文件，而是根据时间，文件大小或者两者，将历史文件打包输出。

```xml
<property name="LOG_FILE" value="LogFile" />
    <property name="LOG_DIR" value="/var/logs/application" />
    <appender name="ROLL_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每日滚动打包 -->
            <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.gz</fileNamePattern>
            <!-- 保存30天日志，文件总大小在3GB -->
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>
```
这里定义了文件路径和文件名，<rollingPolicy >使用基于时间的TimeBasedRollingPolicy，<fileNamePattern>不仅定义文件名，而且定义了每天打包一次文件。也可以按月打包一次：<fileNamePattern>${LOG_DIR}/%d{yyyy/MM}/${LOG_FILE}.gz</fileNamePattern>
## 6.4 自定义Appender
参考这里：https://www.baeldung.com/custom-logback-appender。

# 7 Layouts
Layouts用于格式化log信息。也可以自定义Layout，但通常我们使用默认的PatternLayout 。

```xml
<encoder>
    <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
</encoder>
```
- %d{HH:mm:ss.SSS}，表示小时，分钟，秒，毫秒；
- [%thread]  ，表示线程；
- %-5level，表示日志level；
- %logger{36}，表示logger的名，截取35个字符；
- %msg%n，%msg日志信息，%n表示换行。
可以参考：http://logback.qos.ch/manual/layouts.html#conversionWord
# 8 总结
我们学习了Logback的基本功能，了解了三个组件：Logger，Appender和Layouts，比较详细讲解了主要使用的FileAppender和RollingFileAppender.
代码地址：https://github.com/tobebetter9527/LogbackDemo。