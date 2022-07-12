package com.codect.authService.rest;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GlobalMethodSecurityAdapter extends GlobalMethodSecurityConfiguration {

	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JdbcMutableAclService aclService() {
		JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(ds, lookupStrategy(), aclCache());
		jdbcMutableAclService.setInsertObjectIdentitySql("insert into acl_object_identity "
				+ "(object_id_class, object_id_identity, owner_sid, entries_inheriting) values (?, cast(? as bigint), ?, ?)");
		jdbcMutableAclService.setFindChildrenQuery("select obj.object_id_identity as obj_id, class.class as class "+
				"from acl_object_identity obj, acl_object_identity parent, acl_class class "
				+ " where obj.parent_object = parent.id and obj.object_id_class = class.id "
				+ "	and parent.object_id_identity = cast(? as bigint) and parent.object_id_class = ("
				+ "	select id FROM acl_class where acl_class.class = ?)");
		jdbcMutableAclService.setObjectIdentityPrimaryKeyQuery("select acl_object_identity.id from acl_object_identity, acl_class "
				+ "where acl_object_identity.object_id_class = acl_class.id and acl_class.class=? "
				+ "and acl_object_identity.object_id_identity = cast(? as bigint)");
		return jdbcMutableAclService;
	}

	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	public PermissionGrantingStrategy permissionGrantingStrategy() {
		return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
	}

	public EhCacheBasedAclCache aclCache() {
		return new EhCacheBasedAclCache(aclEhCacheFactoryBean().getObject(), permissionGrantingStrategy(),
				aclAuthorizationStrategy());
	}

	@Bean
	public EhCacheFactoryBean aclEhCacheFactoryBean() {
		EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
		ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
		ehCacheFactoryBean.setCacheName("aclCache");
		return ehCacheFactoryBean;
	}

	public EhCacheManagerFactoryBean aclCacheManager() {
		return new EhCacheManagerFactoryBean();
	}

	@Autowired
	private DataSource ds;

	public LookupStrategy lookupStrategy() {
		BasicLookupStrategy basicLookupStrategy = new BasicLookupStrategy(ds, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
//		basicLookupStrategy.setAclClassIdSupported(true);
		basicLookupStrategy.setLookupObjectIdentitiesWhereClause("(CAST(acl_object_identity.object_id_identity AS VARCHAR)= ? and acl_class.class = ?)");
		return basicLookupStrategy;
	}

	@Override
	public MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
		expressionHandler.setPermissionEvaluator(permissionEvaluator);
		expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
		return expressionHandler;
	}
}
