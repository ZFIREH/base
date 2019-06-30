package cn.com.zhouhuo;

import cn.com.zhouhuo.bean.Trader;
import cn.com.zhouhuo.bean.Transaction;
import com.alibaba.fastjson.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * 找出2016年发生的所有交易，并按交易额从低到高排列
     * filter(Predicate):Stream ：词筛选。Predicate 就是函数式接口，可用lamdba表达式
     * sorted():Stream:自定义比较
     * collect(Collectores):终端,结束流
     */
    @Test
    public void demo1() {
        List<Transaction> result = transactions.stream().filter(s -> s.getYear() == 2016).sorted(Comparator.comparing(Transaction::getValue)).collect(Collectors.toList());
        System.out.println(JSONArray.toJSONString(result));
    }

    /**
     * 问题二 交易员都在那些不同的城市工作
     * 重点：map的作用，可以转换类型。还有去重复
     * 知识点：
     *      map(Function):Stream：接收一个函数作参数，该函数会将每一个元素传入的值映射成另外的一个元素，按照1:1的比例。
     *      distinct():Stream：会去除相同的元素，根据元素的hashCode和equals方法实现。
     */
    @Test
    public void demo2(){
        List<String> result = transactions.stream().map(transaction -> transaction.getTrader().getCity()).distinct().collect(Collectors.toList());
        System.out.println(JSONArray.toJSONString(result));
    }

    /**
     * 问题三 查找所有来自于广州工作的交易员，并按名字排序
     * 重点：获取关键词，关键词排序
     */
    @Test
    public void demo3(){
        List<Trader> result = transactions.stream().map(Transaction::getTrader).filter(trader -> "广州".equals(trader.getCity())).sorted(Comparator.comparing(Trader::getName)).distinct().collect(Collectors.toList());
        System.out.println(JSONArray.toJSONString(result));

    }

    /**
     * 问题四 返回所有交易员的姓名字符串，按字母顺序排序
     * 重点： reduce的作用，可以把元素组合起来
     * 知识点：reduce(BinaryOperator)Optional：这个方法的主要作用是把 Stream 元素组合起来。它提供一个起始值（种子），
     *        然后依照运算规则（BinaryOperator），和前面 Stream 的第一个、第二个、第 n 个元素组合。
     */
    @Test
    public void demo4(){
        String result = transactions.stream().map(transaction -> transaction.getTrader().getName()).sorted(Comparator.comparing(String::toString)).distinct().reduce("", (n1, n2) -> n1 + n2);
        System.out.println(result);
    }

    /**
     * 问题五 有没有交易员在肇庆工作,返回boolean
     * 知识点：anyMath的作用，判断至少有一个 存在
     */
    @Test
    public void demo5(){
        boolean result = transactions.stream().anyMatch(transaction -> transaction.getTrader().getCity().equals("肇庆"));
        System.out.println(result);
    }

    /**
     * 问题六 打印生活在广州的交易员的所有交易额
     * 知识点：
     * forEach(Consumer):遍历每一个元素
     */
    @Test
    public void demo6(){
        transactions.stream().filter(transaction -> "广州".equals(transaction.getTrader().getCity())).map(Transaction::getValue).forEach(System.out::println);
    }

    /**
     * 问题七 所有交易中，最高的交易额是多少
     * 重点：reduce的运用 max
     */
    @Test
    public void demo7(){
        Optional<Integer> reduce = transactions.stream().map(Transaction::getValue).reduce(Integer::max);
        System.out.println(reduce.get());
    }

    /**
     * 问题八 找到交易额最小的交易
     * 重点：reduce的运用 min
     */
    @Test
    public void demo8(){
        Integer result = transactions.stream().map(Transaction::getValue).reduce(Integer::min).get();
        System.out.println(result);
    }
}
