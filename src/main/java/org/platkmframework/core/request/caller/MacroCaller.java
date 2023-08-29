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
 **/
public class MacroCaller 
{
	
/*	@AutoWired
	private MethodCaller methodCaller; 
	
	@AutoWired
	private AppResourcesService appResourcesService;

	public MacroCaller(){}

	*//**
	 * 
	 * @param generatedCode
	 * @param methodName
	 * @param macroId
	 * @param req
	 * @param resp
	 * @param param
	 * @return
	 * @throws RequestManagerException 
	 * @throws ReflectionError 
	 * @throws IOException 
	 * @throws LanguageFileGenerationInitException 
	 * @throws GenerationException 
	 * @throws JsonException 
	 * @throws ParseException 
	 *//*
	
	
	public String execute(String generatedCode, 
			              String methodName, 
						  String macroName,
						  HttpServletRequest req, HttpServletResponse resp,
						  Map<String, Object> param) throws RequestManagerException, ReflectionError, IOException, JsonException, ParseException 
	{
		
		AppResourcesVO resource = appResourcesService.loadResourceByCode(generatedCode); 
		if(resource == null)
			throw new RequestManagerException("resources not found");
		
		Object obj = ObjectContainer.instance().getSessionObjectByPath(resource.getPath());
		if(obj == null)
			throw new RequestManagerException("backend not found");
		  
		if(StringUtils.isNotEmpty(methodName))
			methodCaller.execute(resource.getPath(), methodName, req, resp, param);
		
		param.put(PageContent.C_BACKEND_OBJECT_KEY, obj);
		
		String realPath     = PageContent.instance().get(resource.getResourceDomain());
		String pageFilePath = realPath + File.separator + resource;
		File file = new File(pageFilePath);
		
		File tempFile = new File(file.getParent() + File.separator + Util.generateId() + ".vm");
		FileUtils.writeStringToFile(tempFile, "#include \"" +  file.getName() + "\" #" + macroName + "()" , "UTF-8");
		 
		VelocityContext context = _getVelocityContent(param);
		LanguageFileGeneration v = GeneratorManager.instance().getVelocity(file.getParent() ,context);
		return v.generate(tempFile.getAbsolutePath()); 
		 
	}

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
	}	 
 */
}
