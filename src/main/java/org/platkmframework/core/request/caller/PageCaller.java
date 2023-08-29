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
package org.platkmframework.core.request.caller;



/**
 *   Author: 
 *     Eduardo Iglesias
 *   Contributors: 
 *   	Eduardo Iglesias - initial API and implementation
 * method used when a page is requested
 * @author Eduardo Iglesias Taylor
 *
 */
 
//@Service
public class PageCaller 
{ 
/*	@AutoWired
	private MethodCaller methodCaller; 
	
	@AutoWired
	private AppResourcesService appResourcesService;
	
	public PageCaller(){}


	*//**
	 * 
	 * @param filePathName
	 * @param domain
	 * @param req
	 * @param resp
	 * @param param
	 * @return
	 * @throws RequestManagerException 
	 * @throws ReflectionError 
	 * @throws LanguageFileGenerationInitException 
	 * @throws GenerationException 
	 * @throws JsonException 
	 * @throws IOException 
	 * @throws ParseException 
	 *//*
	public String exectue(String generatedCode, String methodName, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> param) throws RequestManagerException, ReflectionError, GenerationException, LanguageFileGenerationInitException, IOException, JsonException, ParseException 
	{ 
		AppResourcesVO resource = appResourcesService.loadResourceByCode(generatedCode); 
		return exectue(resource, methodName, req, resp, param);
	}
	
	*//**
	 * 
	 * @param resource
	 * @param methodName
	 * @param req
	 * @param resp
	 * @param param
	 * @return
	 * @throws RequestManagerException
	 * @throws ReflectionError
	 * @throws GenerationException
	 * @throws LanguageFileGenerationInitException
	 * @throws IOException
	 * @throws JsonException
	 * @throws ParseException
	 *//*
	public String exectue(AppResourcesVO resource, String methodName, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> param) throws RequestManagerException, ReflectionError, GenerationException, LanguageFileGenerationInitException, IOException, JsonException, ParseException 
	{ 
		
		if(resource == null)
			throw new RequestManagerException("resources not found");
		
		Object obj = ObjectContainer.instance().getSessionObjectByPath(resource.getPath());
		if(obj == null)
			throw new RequestManagerException("backend not found");
		
		AttributeSetter attributeSetter = new AttributeSetter();
		attributeSetter.execute(obj, req, resp, param);
		
		if(StringUtils.isNotEmpty(methodName))
			methodCaller.execute(obj, methodName, req, resp, param);
				
		String realPath     = PageContent.instance().get(resource.getResourceDomain());
		String pageFilePath = realPath + File.separator + resource.getName();
		
		File file = new File(pageFilePath.replace("/", File.separator).replace("\n", File.separator));
		 
		param.put(PageContent.C_BACKEND_OBJECT_KEY, obj);
		return generateResult(file.getParent(), file.getName(), req, resp, param);
	}
 
 
	*//**
	 * 
	 * @param folderVelocity
	 * @param file
	 * @param req
	 * @param resp
	 * @param param
	 * @return
	 * @throws GenerationException
	 * @throws LanguageFileGenerationInitException
	 * @throws RequestManagerException
	 * @throws ReflectionError
	 * @throws JsonException 
	 * @throws IOException 
	 * @throws ParseException 
	 *//*
	public String generateResult(String folderVelocity, String fileName, 
			HttpServletRequest req, HttpServletResponse resp,
			Map<String, Object> param) throws GenerationException, LanguageFileGenerationInitException, RequestManagerException, ReflectionError, IOException, JsonException, ParseException
	{ 
	
		VelocityContext context = _getVelocityContent(param);
		LanguageFileGeneration v = GeneratorManager.instance().getVelocity(folderVelocity,context);
		String result = v.generate(fileName);  		
	
		//check redirection
		String redirectPath = SessionContentManager.instance().getForcePage();
		if(StringUtils.isNotEmpty(redirectPath))
		{
			AppResourcesVO resource = appResourcesService.loadResourceByPath(redirectPath); 
			if(resource == null)
				throw new RequestManagerException("resources not found");
			
			String methodName = SessionContentManager.instance().getForceMethod();
			if(StringUtils.isNotEmpty(methodName))
				methodCaller.execute(resource.getPath(), methodName, req, resp, param);
			 
			context = _getVelocityContent(param);
			v = GeneratorManager.instance().getVelocity(folderVelocity,context);
			return v.generate(fileName);  
		}
		else
			return result;
	}		
 
	*//**
	 * 
	 * @param param
	 * @return
	 *//*
	private VelocityContext _getVelocityContent(Map<String, Object> param) 
	{
		VelocityContext context = new VelocityContext();
        if(param != null && param.size()>0)
        {
           Iterator<String> contextMapIter = param.keySet().iterator();
           while (contextMapIter.hasNext()) 
           {
               String key = (String) contextMapIter.next();
               context.put(key, param.get(key));
           }
        }
        return context;
	}*/
}
