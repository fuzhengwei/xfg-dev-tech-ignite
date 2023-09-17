package cn.bugstack.xfg.dev.tech.trigger.http;

import cn.bugstack.xfg.dev.tech.infrastructure.mysql.dao.IMySQLUserOrderDao;
import cn.bugstack.xfg.dev.tech.infrastructure.mysql.po.UserOrderPO;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * ab -c 20 -n 1000 http://127.0.0.1:8091/api/mysql/cacheData
 * ab -c 50 -n 1000 http://127.0.0.1:8091/api/mysql/selectByOrderId
 */
@Slf4j
@RestController("mysql")
@RequestMapping("/api/mysql/")
public class MySQLController {

    @Resource
    private IMySQLUserOrderDao mySQLUserOrderDao;

    // 缓存数据，从库中拉取。
    private final List<UserOrderPO> userOrderPOList = Collections.synchronizedList(new ArrayList<>());

    /**
     * 随机取数据，加入缓存测试
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 20 -n 2000 http://127.0.0.1:8091/api/mysql/cacheData
     */
    @RequestMapping(value = "cacheData", method = RequestMethod.GET)
    public String cacheData() {
        Long maxId = mySQLUserOrderDao.queryMaxId();
        long idx = new Random().nextLong() % maxId;
        if (idx < 0) {
            idx += maxId;
        }
        UserOrderPO userOrderPO = mySQLUserOrderDao.selectById(idx);
        userOrderPOList.add(userOrderPO);
        log.info("cache 存储数据到缓存，用于查询、更新测试 count：{} ", userOrderPOList.size());
        return "done! 缓存数量：" + userOrderPOList.size();
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 100 -n 60000 http://127.0.0.1:8091/api/mysql/insert
     */
    @RequestMapping("insert")
    public String insert() {
        log.info("性能测试，insert");

        UserOrderPO userOrderPO = UserOrderPO.builder()
                .userName("小傅哥")
                .userId("xfg" .concat(RandomStringUtils.randomNumeric(3)))
                .userMobile("+86 1352140" .concat(String.format("%04d", new Random().nextInt(1000))))
                .sku("13811216")
                .skuName("《手写MyBatis：渐进式源码实践》")
                .orderId(RandomStringUtils.randomNumeric(11))
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(128))
                .discountAmount(BigDecimal.valueOf(50))
                .tax(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(78))
                .orderDate(new Date())
                .orderStatus(0)
                .isDelete(0)
                .uuid(UUID.randomUUID().toString().replace("-", ""))
                .ipv4("127.0.0.1")
                .ipv6("2001:0db8:85a3:0000:0000:8a2e:0370:7334" .getBytes())
                .extData("{\"device\": {\"machine\": \"IPhone 14 Pro\", \"location\": \"shanghai\"}}")
                .build();

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.insert(userOrderPO);
        stopwatch.stop();

        log.info("性能测试，insert 耗时: {}", stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 1 -n 1 http://127.0.0.1:8091/api/mysql/updateOrderStatusByUserId
     */
    @RequestMapping("updateOrderStatusByUserId")
    public String updateOrderStatusByUserId() {
        log.info("性能测试，update");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.updateOrderStatusByUserId(userOrderPO.getUserId());
        stopwatch.stop();

        log.info("性能测试，updateOrderStatusByUserMobile userId:{} 耗时: {}", userOrderPO.getUserId(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 1 -n 1 http://127.0.0.1:8091/api/mysql/updateOrderStatusByUserMobile
     */
    @RequestMapping("updateOrderStatusByUserMobile")
    public String updateOrderStatusByUserMobile() {
        log.info("性能测试，update");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.updateOrderStatusByUserMobile(userOrderPO.getUserMobile());
        stopwatch.stop();

        log.info("性能测试，updateOrderStatusByUserMobile userMobile:{} 耗时: {}", userOrderPO.getUserMobile(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 20 -n 2000 http://127.0.0.1:8091/api/mysql/updateOrderStatusByOrderId
     */
    @RequestMapping("updateOrderStatusByOrderId")
    public String updateOrderStatusByOrderId() {
        log.info("性能测试，update");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.updateOrderStatusByOrderId(userOrderPO.getOrderId());
        stopwatch.stop();

        log.info("性能测试，updateOrderStatusByOrderId orderId:{} 耗时: {}", userOrderPO.getOrderId(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 1 -n 1 http://127.0.0.1:8091/api/mysql/selectByUserId
     */
    @RequestMapping("selectByUserId")
    public String selectByUserId() {
        log.info("性能测试，update");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.selectByUserId(userOrderPO.getOrderId());
        stopwatch.stop();

        log.info("性能测试，selectByUserId orderId:{} 耗时: {}", userOrderPO.getOrderId(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 20 -n 50 http://127.0.0.1:8091/api/mysql/selectByUserMobile
     */
    @RequestMapping("selectByUserMobile")
    public String selectByUserMobile() {
        log.info("性能测试，selectByUserMobile");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.selectByUserMobile(userOrderPO.getUserMobile());
        stopwatch.stop();

        log.info("性能测试，selectByUserMobile userMobile:{} 耗时: {}", userOrderPO.getUserMobile(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 20 -n 2000 http://127.0.0.1:8091/api/mysql/selectByOrderId
     * 2.143 seconds
     */
    @RequestMapping("selectByOrderId")
    public String selectByOrderId() {
        log.info("性能测试，selectByOrderId");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.selectByOrderId(userOrderPO.getOrderId());
        stopwatch.stop();

        log.info("性能测试，selectByOrderId orderId:{} 耗时: {}", userOrderPO.getOrderId(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 20 -n 500 http://127.0.0.1:8091/api/mysql/selectByOrderIdAndUserId
     */
    @RequestMapping("selectByOrderIdAndUserId")
    public String selectByOrderIdAndUserId() {
        log.info("性能测试，selectByOrderIdAndUserId");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.selectByOrderIdAndUserId(userOrderPO);
        stopwatch.stop();

        log.info("性能测试，selectByOrderIdAndUserId orderId:{} userId:{} 耗时: {}", userOrderPO.getOrderId(), userOrderPO.getUserId(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

    /**
     * ab：ApacheBench工具的命令。
     * -c 20：并发请求数为20，表示同时发送20个请求。
     * -n 600：总请求数为60，表示发送60个请求。
     * <p>
     * ab -c 20 -n 50 http://127.0.0.1:8091/api/mysql/selectByUserIdAndOrderId
     */
    @RequestMapping("selectByUserIdAndOrderId")
    public String selectByUserIdAndOrderId() {
        log.info("性能测试，selectByUserIdAndOrderId");
        UserOrderPO userOrderPO = userOrderPOList.remove(0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        mySQLUserOrderDao.selectByUserIdAndOrderId(userOrderPO);
        stopwatch.stop();

        log.info("性能测试，selectByUserIdAndOrderId userId:{} orderId:{} 耗时: {}", userOrderPO.getUserId(), userOrderPO.getOrderId(), stopwatch);
        return "done! 耗时：" + stopwatch;
    }

}
