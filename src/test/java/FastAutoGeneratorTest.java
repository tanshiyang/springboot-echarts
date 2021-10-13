import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

/**
 * <p>
 * 快速生成
 * </p>
 *
 * @author lanjerry
 * @since 2021-09-16
 */
public class FastAutoGeneratorTest {

    /**
     * 数据源配置
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig.
            Builder("jdbc:mysql://127.0.0.1:3306/mystock?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&useOldAliasMetadataBehavior=true"
            ,"root","123123");

    @Test
    public  void Test01() throws SQLException {
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                .globalConfig(builder -> {
                    builder.author("tanshiyang") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://code//generate"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.tanshiyang") // 设置父包名
                            .moduleName("rps") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D://")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("rps_tops") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}