package tool;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class WuhanElectricResourcesGenerator {

    public static void main(String[] args) throws Exception {

		String outputDir = "D:\\view";
		final String viewOutputDir = outputDir ;
		Class<AutoGenerator> autoGeneratorClass = AutoGenerator.class;
		AutoGenerator mpg = autoGeneratorClass.newInstance();
		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(outputDir);
		gc.setFileOverride(true);
		gc.setActiveRecord(true);
		// XML 二级缓存
		gc.setEnableCache(false);
		// XML ResultMap
		gc.setBaseResultMap(true);
		// XML columList
		gc.setBaseColumnList(true);
		gc.setAuthor("hcy");
		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.MYSQL);
		//dsc.setDriverName("oracle.jdbc.driver.OracleDriver");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
		//dsc.setUsername("WUHAN");
		//dsc.setPassword("WUHAN");
		dsc.setUsername("root");
		dsc.setPassword("root");
		//dsc.setUrl("jdbc:oracle:thin:@192.168.0.129:1521:monkey");
		dsc.setUrl("jdbc:mysql://localhost:3306/star_node?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC");
		mpg.setDataSource(dsc);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		// 此处可以修改为您的表前缀
		strategy.setInclude(new String[]{"equipment"});

		String myName = "equipment";

		// 全局大写命名 ORACLE 注意
		strategy.setCapitalMode(true);
		strategy.setSuperControllerClass("com.mg.sn.mill.controller");
		strategy.setSuperServiceClass("com.mg.sn.mill.service");
		strategy.setSuperServiceImplClass("com.mg.sn.mill.service.impl");
		//添加注解
		strategy.entityTableFieldAnnotationEnable(true);
		// 表名生成策略
		strategy.setNaming(NamingStrategy.underline_to_camel);
		List<TableFill> fillList=new ArrayList<TableFill>();
		TableFill t=new TableFill("", FieldFill.INSERT_UPDATE);
		fillList.add(t);
		strategy.setTableFillList(fillList);
		strategy.setEntityLombokModel(true);
		mpg.setStrategy(strategy);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setParent("com.mg.sn.mill");
		pc.setController("controller");
		pc.setMapper("mapper");
		pc.setEntity("model.entity");
		pc.setService("service");
		pc.setServiceImpl("service.impl");
		pc.setXml("dao.xml");
		mpg.setPackageInfo(pc);

		// 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
			}
		};
		// 生成的模版路径，不存在时需要先新建
		File viewDir = new File(viewOutputDir);
		if (!viewDir.exists()) {
			viewDir.mkdirs();
		}
//		List<FileOutConfig> focList = new ArrayList<FileOutConfig>();
//		focList.add(new FileOutConfig("/templates/listvue.vue.vm") {
//			@Override
//			public String outputFile(TableInfo tableInfo) {
//				changename(strategy,tableInfo,myName);
//				return getGeneratorViewPath(viewOutputDir, tableInfo, ".vue");
//			}
//		});
//		focList.add(new FileOutConfig("/templates/service.java.vm") {
//			@Override
//			public String outputFile(TableInfo tableInfo) {
//				changename(strategy,tableInfo,myName);
//				return getGeneratorViewPath(viewOutputDir, tableInfo, ".vue");
//			}
//		});
//		focList.add(new FileOutConfig("/templates/serviceImpl.java.vm") {
//			@Override
//			public String outputFile(TableInfo tableInfo) {
//				changename(strategy,tableInfo,myName);
//				return getGeneratorViewPath(viewOutputDir, tableInfo, ".vue");
//			}
//		});
//		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 生成controller相关
		ConfigBuilder config = mpg.getConfig();

//		logger.debug("==========================准备生成文件...==========================");
		// 初始化配置

		if (null == config) {
			config = new ConfigBuilder(mpg.getPackageInfo(), mpg.getDataSource(), strategy, mpg.getTemplate(), mpg.getGlobalConfig());
			Field injectionConfig = autoGeneratorClass.getDeclaredField("injectionConfig");
			injectionConfig.setAccessible(true);
			if (null != injectionConfig.get(mpg)) {
				Class<InjectionConfig> injectionConfigClass = InjectionConfig.class;
				Method setConfig = injectionConfigClass.getDeclaredMethod("setConfig", ConfigBuilder.class);
				setConfig.invoke(injectionConfig.get(mpg),config);
			}
		}
		AbstractTemplateEngine templateEngine = mpg.getTemplateEngine();
		if (null == templateEngine) {
			// 为了兼容之前逻辑，采用 Velocity 引擎 【 默认 】
			templateEngine = new VelocityTemplateEngine();
		}
		// 模板引擎初始化执行文件输出

		Method pretreatmentConfigBuilder = autoGeneratorClass.getDeclaredMethod("pretreatmentConfigBuilder", ConfigBuilder.class);
		pretreatmentConfigBuilder.setAccessible(true);
		AbstractTemplateEngine mkdirs = templateEngine.init(((ConfigBuilder) pretreatmentConfigBuilder.invoke(mpg, config))).mkdirs();
		batchOutput(mkdirs,strategy,myName).open();
//		logger.debug("==========================文件生成完成！！！==========================");
//		mpg.execute();
	}

    /**
     * 获取配置文件
     *
     * @return 配置Props
     */
    private static Properties getProperties() {
        // 读取配置文件
        Resource resource = new ClassPathResource("application-dev.yml");
        Properties props = new Properties();
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     * 页面生成的文件名
     */
    private static String getGeneratorViewPath(String viewOutputDir, TableInfo tableInfo, String suffixPath) {
        String name = StringUtils.firstToLowerCase(tableInfo.getEntityName());
        String path = viewOutputDir + "/" + name + "/index" + suffixPath;
        File viewDir = new File(path).getParentFile();
        if (!viewDir.exists()) {
            viewDir.mkdirs();
        }
        return path;
    }

    private static TableInfo changename(StrategyConfig strategy,TableInfo tableInfo, String myName) {
        tableInfo.setEntityName(strategy,myName);

        String mapperName = tableInfo.getMapperName();
        mapperName = myName + mapperName.substring(mapperName.lastIndexOf("M"),mapperName.length());
        tableInfo.setMapperName(mapperName);

        String xmlName = tableInfo.getXmlName();
        xmlName = myName + xmlName.substring(xmlName.lastIndexOf("M"),xmlName.length());
        tableInfo.setXmlName(xmlName);

        String serviceName = tableInfo.getServiceName();
        serviceName ="I"+ myName + serviceName.substring(serviceName.lastIndexOf("S"),serviceName.length());
        tableInfo.setServiceName(serviceName);

        String serviceImplName = tableInfo.getServiceImplName();
        serviceImplName = myName + serviceImplName.substring(serviceImplName.lastIndexOf("S"),serviceImplName.length());
        tableInfo.setServiceImplName(serviceImplName);

        String controllerName = tableInfo.getControllerName();
        controllerName = myName + controllerName.substring(controllerName.lastIndexOf("C"),controllerName.length());
        tableInfo.setControllerName(controllerName);

        return tableInfo;
    }

    public static AbstractTemplateEngine batchOutput(AbstractTemplateEngine mkdirs,StrategyConfig strategy,String myName) throws Exception{
        List<TableInfo> tableInfoList = mkdirs.getConfigBuilder().getTableInfoList();
        for (TableInfo tableInfo : tableInfoList) {
            Map<String, Object> objectMap = mkdirs.getObjectMap(tableInfo);
            Map<String, String> pathInfo = mkdirs.getConfigBuilder().getPathInfo();
            TemplateConfig template = mkdirs.getConfigBuilder().getTemplate();
            changename(strategy,tableInfo,myName);
            String entityName = tableInfo.getEntityName();
            if (null != entityName) {
                String entityFile = String.format((pathInfo.get(ConstVal.ENTITY_PATH) + File.separator + "%s" +suffixJavaOrKt(mkdirs)), entityName);
                if (isCreate(mkdirs,entityFile)) {
                    mkdirs.writer(objectMap, mkdirs.templateFilePath(template.getEntity(mkdirs.getConfigBuilder().getGlobalConfig().isKotlin())), entityFile);
                }
            }
            // MpMapper.java
            if (null != tableInfo.getMapperName()) {
                String mapperFile = String.format((pathInfo.get(ConstVal.MAPPER_PATH) + File.separator + tableInfo.getMapperName() + suffixJavaOrKt(mkdirs)), entityName);
                if (isCreate(mkdirs,mapperFile)) {
                    mkdirs.writer(objectMap, mkdirs.templateFilePath(template.getMapper()), mapperFile);
                }
            }
            // MpMapper.xml
            if (null != tableInfo.getXmlName()) {
                String xmlFile = String.format((pathInfo.get(ConstVal.XML_PATH) + File.separator + tableInfo.getXmlName() + ConstVal.XML_SUFFIX), entityName);
                if (isCreate(mkdirs,xmlFile)) {
                    mkdirs.writer(objectMap, mkdirs.templateFilePath(template.getXml()), xmlFile);
                }
            }
            // IMpService.java
            if (null != tableInfo.getServiceName()) {
                String serviceFile = String.format((pathInfo.get("service_impl_path") + File.separator + tableInfo.getServiceName() + suffixJavaOrKt(mkdirs)), entityName);
                if (isCreate(mkdirs,serviceFile)) {
                    mkdirs.writer(objectMap, mkdirs.templateFilePath(template.getService()), serviceFile);
                }
            }
            // MpServiceImpl.java
            if (null != tableInfo.getServiceImplName()) {
                String implFile = String.format((pathInfo.get("service_impl_path") + File.separator + tableInfo.getServiceImplName() + suffixJavaOrKt(mkdirs)), entityName);
                if (isCreate(mkdirs,implFile)) {
                    mkdirs.writer(objectMap, mkdirs.templateFilePath(template.getServiceImpl()), implFile);
                }
            }
            // MpController.java
            if (null != tableInfo.getControllerName()) {
                String controllerFile = String.format((pathInfo.get(ConstVal.CONTROLLER_PATH) + File.separator + tableInfo.getControllerName() + suffixJavaOrKt(mkdirs)), entityName);
                if (isCreate(mkdirs,controllerFile)) {
                    mkdirs.writer(objectMap, mkdirs.templateFilePath(template.getController()), controllerFile);
                }
            }
            // 自定义内容
            if (null != mkdirs.getConfigBuilder().getInjectionConfig()) {
                List<FileOutConfig> focList = mkdirs.getConfigBuilder().getInjectionConfig().getFileOutConfigList();
                if (CollectionUtils.isNotEmpty(focList)) {
                    for (FileOutConfig foc : focList) {
                        if (isCreate(mkdirs,foc.outputFile(tableInfo))) {
                            mkdirs.writer(objectMap, foc.getTemplatePath(), foc.outputFile(tableInfo));
                        }
                    }
                }
            }
        }
        return mkdirs;
    }

    private static String suffixJavaOrKt(AbstractTemplateEngine mkdirs) {
        return mkdirs.getConfigBuilder().getGlobalConfig().isKotlin() ? ConstVal.KT_SUFFIX : ConstVal.JAVA_SUFFIX;
    }

    private static boolean isCreate(AbstractTemplateEngine mkdirs,String filePath) {
        File file = new File(filePath);
        return !file.exists() || mkdirs.getConfigBuilder().getGlobalConfig().isFileOverride();
    }
}
