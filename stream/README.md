

[TOC]



## 什么是流

1. java.util.Stream的一个接口，简称流，可以处理数据更加方便，可以看成遍历数据集的高级迭代器；
2. 提供串行和并行两种模式进行汇聚操作，并发模式能够充分利用多核处理器的优势；
3. Stream 可以并行化操作，迭代器只能命令式地、串行化操作；
4. Stream 的另外一大特点是，数据源本身可以是无限的；
5. 流的结构；

![stream-process](https://oss.zhouhuo.com.cn/Fh5PVqKYU29U8SmJjKLZ6swyfKoc)

## 8个例子用上流

先建立两个bean类（可跳过不看）

[Trader.java](https://github.com/ZFIREH/base/blob/master/stream/src/main/java/cn/com/zhouhuo/bean/Trader.java)

```java
package cn.com.zhouhuo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouhuo
 * @date 2019/6/29 19:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trader {
    private String name;
    private String city;
}
```

[Transaction.java](<https://github.com/ZFIREH/base/blob/master/stream/src/main/java/cn/com/zhouhuo/bean/Transaction.java>)

```java
package cn.com.zhouhuo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouhuo
 * @date 2019/6/29 19:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Trader trader;
    private int year;
    private int value;
}

```

准备数据

```java
/**
 * @author zhouhuo
 * @date 2019/6/29 20:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StreamBoot.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StreamTestBoot {

    List<Transaction> transactions = null;

    @Before
    public void init() {
        Trader xiaoming = new Trader("小明", "广州");
        Trader xiaohong = new Trader("小红", "广州");
        Trader xiaohei = new Trader("小黑", "广州");
        Trader xiaobai = new Trader("小白", "肇庆");
        this.transactions = Arrays.asList(
                new Transaction(xiaoming, 2017, 300),
                new Transaction(xiaohong, 2016, 1000),
                new Transaction(xiaohong, 2017, 400),
                new Transaction(xiaohei, 2016, 710),
                new Transaction(xiaohei, 2016, 700),
                new Transaction(xiaobai, 2016, 950)
        );
    }
}
```



例：[StreamTestBoot.java](<https://github.com/ZFIREH/base/blob/master/stream/src/test/java/cn/com/zhouhuo/StreamTestBoot.java>)

1. 问题一，找出2016年发生的所有交易，并按交易额从低到高排列

   - 重点：考擦筛选和排序

   - 知识点：

     - `filter(Predicate):Stream` ：词筛选。Predicate 就是函数式接口，可用lamdba表达式

     - `sorted():Stream`:自定义比较
     - `collect(Collectores)`:终端,结束流

   - 代码：

   ```java
   @Test
   public void demo1() {
   	List<Transaction> result = transactions.stream().filter(s -> s.getYear() == 2016).sorted(Comparator.comparing(Transaction::getValue)).collect(Collectors.toList());
   	System.out.println(JSONArray.toJSONString(result));
   }
   ```

   

2. 问题二 交易员都在那些不同的城市工作

   - 重点：map的作用，可以转换类型。还有去重复

   - 知识点： 
     -  `map(Function):Stream`：接收一个函数作参数，该函数会将每一个元素传入的值映射成另外的一个元素，按照1:1的比例。
     -  `distinct():Stream`：会去除相同的元素，根据元素的hashCode和equals方法实现。

   - 代码：

   ```java
   @Test
   public void demo2(){
   	List<String> result = transactions.stream().map(transaction -> transaction.getTrader().getCity()).distinct().collect(Collectors.toList());
   	System.out.println(JSONArray.toJSONString(result));
   }
   ```

   

3. 问题三 查找所有来自于广州工作的交易员，并按名字排序

   - 重点：获取关键词，关键词排序
   - 代码：

   ```java
   @Test
   public void demo3(){
       List<Trader> result = transactions.stream().map(Transaction::getTrader).filter(trader -> "广州".equals(trader.getCity())).sorted(Comparator.comparing(Trader::getName)).distinct().collect(Collectors.toList());
       System.out.println(JSONArray.toJSONString(result));
   
   }
   ```

   

4. 问题四 返回所有交易员的姓名字符串，按字母顺序排序

   - 重点： reduce的作用，可以把元素组合起来

   - 知识点：`reduce(BinaryOperator)Optional`：这个方法的主要作用是把 Stream 元素组合起来。它提供一个起始值（种子），然后依照运算规则（**BinaryOperator**），和前面 Stream 的第一个、第二个、第 n 个元素组合。

   - 代码：

   ```java
   @Test
   public void demo4(){
       String result = transactions.stream().map(transaction -> transaction.getTrader().getName()).sorted(Comparator.comparing(String::toString)).distinct().reduce("", (n1, n2) -> n1 + n2);
       System.out.println(result);
   }
   ```

   

5. 问题五 有没有交易员在肇庆工作,返回boolean

   - 知识点：anyMath的作用，判断至少有一个 存在
   - 代码：

   ```java
   @Test
   public void demo5(){
       boolean result = transactions.stream().anyMatch(transaction -> transaction.getTrader().getCity().equals("肇庆"));
       System.out.println(result);
   }
   ```

   

6. 问题六 打印生活在广州的交易员的所有交易额

   - 知识点：
   - `forEach(Consumer)`:遍历每一个元素
   - 代码：

   ```jade
   @Test
   public void demo6(){
       transactions.stream().filter(transaction -> "广州".equals(transaction.getTrader().getCity())).map(Transaction::getValue).forEach(System.out::println);
   }
   ```

   

7. 问题七 所有交易中，最高的交易额是多少

   - 重点：reduce的运用 max
   - 代码：

   ```java
   @Test
   public void demo7(){
       Optional<Integer> reduce = transactions.stream().map(Transaction::getValue).reduce(Integer::max);
       System.out.println(reduce.get());
   }
   ```

   

8. 问题八 找到交易额最小的交易

   - 重点：reduce的运用 min
   - 代码：

   ```java
   @Test
   public void demo8(){
       Integer result = transactions.stream().map(Transaction::getValue).reduce(Integer::min).get();
       System.out.println(result);
   }
   ```

## 流的总结

### 流的中间部分（流处理）

#### 筛选和切片

- `filter(Predicate):Stream` ：词筛选。Predicate 就是函数式接口，可用lamdba表达式
-  `distinct():Stream`：会去除相同的元素，根据元素的hashCode和equals方法实现。
-  `limit(int):Stream`：返回一个不超过给定长度的流，用来获取前N个值。
-  `skip(int):Stream`：返回一个跳掉前面N个值的流，跟limit()方法互补。
-  `sorted():Stream`:自定义比较

#### 映射

- `map(Function):Stream`：接收一个函数作参数，该函数会将每一个元素传入的值映射成另外的一个元素，按照1:1的比例。
- `flatMap(Function):Stream`：一对多的映射，**层级结构扁平化**，就是将最底层元素抽出来放到一起

#### 数值流

- 映射到数值流：mapToInt,mapToDouble和mapToLong.
- 转换回对象流：.boxed()
- 默认值OptionalInt：
- 数值范围：range()和rangeClosed()，这两个方法都是第一个参数时接受起始值，第二个接受结束值。但range()不包含结束值。

#### 构建流

- 由值创建流Stream.of()

- 由数组创建流 Arrays.stream()

- 由文件生成流 Files.lines

- 由函数生成流： 

  - Stream.iterate():iterate 跟 reduce 操作很像，接受一个种子值，和一个 UnaryOperator（例如 f）。然后种子值成为 Stream 的第一个元素，f(seed) 为第二个，f(f(seed)) 第三个，以此类推
  - Stream.generate():通过实现 Supplier 接口，你可以自己来控制流的生成。这种情形通常用于随机数、常量的 Stream，或者需要前后元素间维持着某种状态信息的 Stream。**由于它是无限的，在管道中，必须利用 limit 之类的操作限制 Stream 大小。**

### 终端（结束流的部分）

#### 查找和匹配

- `anyMath(Predicate)boolean`：检查谓词是否至少匹配一个元素
-  `allMatch(Predicate)boolean`：检查谓词是否匹配所有的元素
-  `noneMatch(boolean)Predicate`：确保流中没有任何元素与给定的谓词匹配
-  `findAny()Optional`：将返回当前流中的任意元素
-  `findFirst()Optional`：将返回第一个元素
-  `forEach(Consumer)`:遍历每一个元素
-  `Collect`:对流进行处理



> Optional简介
> Optional<T>类(java.util.Optional)是一个容器类，代表一个值存在或不存在。
> 常用方法：
>
> - isPresent()：将在Optional包含值的时候返回true,否则返回flase。
> - ifPresent(Consumer<T> block)：会在值存在的时候执行给定的代码块。
> - T get()：会在值存在时返回值。
> - T orElse(T other)会在值存在时返回值。

#### 归约

- `reduce(BinaryOperator)Optional`：这个方法的主要作用是把 Stream 元素组合起来。它提供一个起始值（种子），然后依照运算规则（BinaryOperator），和前面 Stream 的第一个、第二个、第 n 个元素组合。

## 列表

| 操作      | 类型 | 返回类型    | 使用的类型/函数式接口  | 函数描述符     |
| --------- | ---- | ----------- | ---------------------- | -------------- |
| filter    | 中间 | Stream<T>   | Predicate<T>           | T -> boolean   |
| distinct  | 中间 | Stream<T>   |                        |                |
| skip      | 中间 | Stream<T>   | long                   |                |
| limit     | 中间 | Stream<T>   | long                   |                |
| map       | 中间 | Stream<R>   | Function<T,R>          | T -> R         |
| flatMap   | 中间 | Stream<R>   | Function<T, Stream<R>> | T -> Stream<R> |
| sorted    | 中间 | Stream<R>   | Comparator<T>          | (T,T) -> int   |
| anyMatch  | 终端 | boolean     | Predicate<T>           | T -> boolean   |
| noneMatch | 终端 | boolean     | Predicate<T>           | T -> boolean   |
| allMatch  | 终端 | boolean     | Predicate<T>           | T -> boolean   |
| findAny   | 终端 | Optional<T> |                        |                |
| findFirst | 终端 | Optional<T> |                        |                |
| forEach   | 终端 | void        | Consumer<T> T -> void  |                |
| collect   | 终端 | R           | Collector<T,A,R>       |                |
| reduce    | 终端 | Optional<T> | BinaryOperator<T>      | (T,T) -> T     |
| count     | 终端 | long        |                        |                |