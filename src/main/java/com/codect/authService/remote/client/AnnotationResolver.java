package com.codect.authService.remote.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Forbidden;

import com.codect.authService.services.JwtService;

@Aspect
@Component
public class AnnotationResolver {

	@Autowired
	HttpServletRequest request;
	@Autowired
	JwtService jwtService;
	@Autowired
	private RemoteClientService rcs;

	@Around("@annotation(FlirPreAuthorize)")
	public Object preAuthorize(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		FlirPreAuthorize myAnnotation = method.getAnnotation(FlirPreAuthorize.class);
		LinkedMultiValueMap<String, String> acls = (LinkedMultiValueMap<String, String>) request.getAttribute("acls");
		String jwtToken = rcs.getJWTToken();
		if (acls == null) {
			acls = createAclsFromJWT(jwtToken);
			request.setAttribute("acls", acls);
		}
		evaluateExpression(joinPoint.getArgs(), signature.getParameterNames(), signature.getParameterTypes(),
				new SpelExpressionParser().parseRaw(myAnnotation.value()).getAST(), acls,jwtToken);
		return joinPoint.proceed();
	}

	/**
	 * for better code use those: 
	 * signature.getParameterTypes() 
	 * new SpelEvaluator(null, null, null).evaluate(null) 
	 * new SpelExpressionParser().parseRaw(myAnnotation.value()).getAST()
	 * 
	 * @param objects
	 * @param strings
	 * 
	 * @param parameterTypes
	 * @param ast
	 * @param acls
	 * @param jwtToken 
	 */
	private void evaluateExpression(Object[] objects, String[] names, Class[] parameterTypes, SpelNode ast,
			LinkedMultiValueMap<String, String> acls, String jwtToken) throws Forbidden {
		String anno = ast.toStringAST();
		
		if (anno.startsWith("hasPermission(")) {
			String params = anno.substring("hasPermission(".length());
			for (int i = 0; i < parameterTypes.length; i++) {
				if (params.startsWith("#" + names[i])) {
					if (objects[i].getClass().equals(parameterTypes[i])) {
						
						String[] perm=anno.split("'");
						Permission buildFromName = new DefaultPermissionFactory().buildFromName(perm[1]);
						Object id=getTheObjectId(objects[i]);
						short cls=rcs.getSecureTypeId(parameterTypes[i].getName());
						String acl=cls+"_"+id+"_"+buildFromName.getMask();
						List<String> perms = acls.get(""+cls);
						if (perms.contains(acl))
							return;
					}
				}
			}
		}
		throw HttpClientErrorException.create(HttpStatus.FORBIDDEN,HttpStatus.FORBIDDEN.name() , null, null, null);
	}

	/**
	 * get the Id from the object.
	 * you can delete it and implement it in your way.
	 * 
	 * @param object
	 * @return
	 */
	private Object getTheObjectId(Object object) {
		Object id=null;
		try {
			Field field = object.getClass().getDeclaredField("id");
			field.setAccessible(true);
			id = field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		}
		return id;
	}

	private LinkedMultiValueMap<String, String> createAclsFromJWT(String jwtToken) {
		LinkedMultiValueMap<String, String> ret = new LinkedMultiValueMap<String, String>();
		if (jwtToken != null) {
			List<String> perms = (List<String>) jwtService.getAllClaimsFromToken(jwtToken).get("acls");
			for (String perm : perms) {
				String type = perm.substring(0, perm.indexOf("_"));
				ret.add(type, perm);
			}
		}
		return ret;
	}
}
