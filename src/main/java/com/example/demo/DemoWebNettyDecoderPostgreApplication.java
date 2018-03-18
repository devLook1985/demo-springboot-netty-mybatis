package com.example.demo;

import java.net.InetSocketAddress;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.example.service.NettyDemoTcpChannelRepository;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@SpringBootApplication
@MapperScan("com.example.service")
@PropertySource(value= "classpath:/nettyserver.properties")
@ChannelHandler.Sharable
public class DemoWebNettyDecoderPostgreApplication{
	
	@Value("${tcp.port}")
	private int tcpPort;
	
	@Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(nettyDemoTcpInitializer);
        
        return b;
    }
	
    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource ds) throws Exception {
    	SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
	    factory.setDataSource(ds);
	    factory.setMapperLocations(
	        new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml")
	    );
	    return factory.getObject();
    }

    @Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
	    return new SqlSessionTemplate(factory);
	}
    
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource ds){
        return new DataSourceTransactionManager(ds);
    }
    
    @Bean(name = "channelRepository")
    public NettyDemoTcpChannelRepository channelRepository() {
        return new NettyDemoTcpChannelRepository();
    }
	
	@Autowired
    @Qualifier("nettyDemoTcpInitializer")
	private NettyDemoTcpInitializer nettyDemoTcpInitializer;
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(DemoWebNettyDecoderPostgreApplication.class, args);
		ctx.getBean(NettyDemoTcpServer.class).start();
	}
}