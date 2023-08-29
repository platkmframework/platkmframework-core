/*******************************************************************************
 *   Copyright(c) 2023 the original author or authors.
 *  
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  
 *        https://www.apache.org/licenses/LICENSE-2.0
 *  
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *******************************************************************************/
package org.platkmframework.core.session;



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 * Se ponen todas las constantes que se usan en la session del usuario
 * @author Eduardo Iglesias Taylor
 *
 */
public interface SessionConstants 
{
	//llave para el content de ApplicationContent
	public static final String C_APP_CONTENT_ID = "APP_CONTENT_ID";
	
	//llave para el content de ApplicationAdmin
	public static final String C_APP_CONTENT_ADMIN_ID = "APP_CONTENT_ADMIN_ID";
	 
	//parametro que indica que el usuario esta tratando de hacer login
	//el valor asociado a la constante esta en el web.xml

	//parametro de login user
    public static final String C_LOGIN_USER_NAME = "_username_";	
    
    //parametro de login password
    public static final String C_LOGIN_USER_PASSWORD = "_password_";	
    
    //constante para guardar el objeto de session del usuarion en la session activa
    public static final String C_USER_SESSION = "USER_SESSION";

	public static final String C_TOKEN = "TOKEN";

	//constante que se pon en el web.xml como init param, contiene la inf del parametro que indica que el usuario est� haciendo login
	public static final String C_LOGIN_REQUEST_PARAMETER = "LOGIN_REQUEST_PARAMETER";
	
	public static final String C_BACKEND_MESSAGE_ERROR = "BACKEND_MESSAGE_ERROR";

	//welcome resource path
	public static final String C_WELCOME_PATH = "/welcome";

	//login resource path
	public static final String C_LOGIN_PATH = "/login";
	
	//logout resource path
	public static final String C_LOGOUT_PATH = "/logout";
	
	//recover pass request resource path
	public static final String C_RECOVER_PASSWORD_PATH = "/recover";
	
	//recover pass check code
	public static final String C_RECOVER_PASSWORD_CHECK_CODE_PATH = "/recover/check/code";
	
	//recover pass save pass
	public static final String C_RECOVER_PASSWORD_CHANGE_PATH = "/recover/update";
	
	//change password resource path
	public static final String C_CHANGE_PASSWORD_PATH = "/change";
	
	//check token recover available
	public static final String C_TOKEN_RECOVER_PATH = "/check-recover-token";
	
	//check token available
	public static final String C_USER_ACTIVE_TOKEN= "/uack";
	
	//check token available (between web service)
	public static final String C_USER_ACTIVE_TOKEN_SERVICE_REQUEST= "/uack-sr";
	
	public static final CharSequence C_BEARER = "Bearer";  
	public static final String C_AUTHORIZATION 		= "Authorization";
	
	public static final String C_PRODUCT_IDENTIFIER = "prdid";
	 
	public static final String C_ACCOUNT_CODE = "acctcode";
}
