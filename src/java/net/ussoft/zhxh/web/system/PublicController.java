package net.ussoft.zhxh.web.system;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ussoft.zhxh.base.BaseConstroller;
import net.ussoft.zhxh.model.PageBean;
import net.ussoft.zhxh.model.Public_content;
import net.ussoft.zhxh.model.Public_pic;
import net.ussoft.zhxh.service.IPublicContentService;
import net.ussoft.zhxh.service.IPublicPicService;
import net.ussoft.zhxh.util.DateUtil;
import net.ussoft.zhxh.util.FileOperate;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**
 * 内容管理  公共
 * @author guodh
 * 
 * */
@Controller
@RequestMapping(value="public")
public class PublicController extends BaseConstroller{

	@Resource
	IPublicPicService picService;	//公共图片
	
	@Resource
	IPublicContentService contentService;  //富文本
	
	private final String PUBLICPIC = "publicpic";		//公共图片获取list
	private final String PUBLICPIC_PAGE = "publicpic_page";	//公共图片获取list 分页
	private final String SUBJECT = "subject";	//专题
	
	/**
	 * 编辑富文本
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public ModelAndView edit(String id,ModelMap modelMap) throws IOException {

		Public_content content = contentService.getById(id);
		
		modelMap.put("content", content);
		
		return new ModelAndView("/view/system/content/uedit", modelMap);
	}
	
	/**
	 * 保存富文本
	 * @param objs
	 * @param response
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@RequestMapping(value="/memo",method=RequestMethod.POST)
	public void save(Public_content content,HttpServletResponse response) throws IOException, IllegalAccessException, InvocationTargetException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "error";
		int num = contentService.update(content);
		if(num >0)
			result = "success";
		out.print(result);
	}
	
	/**
	 * 公共列表 
	 * @param act 用来区分具体功能模块
	 * @param parentid
	 * @param parenttype
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/list",method=RequestMethod.POST)
	public void list(String act,String parentid,String parenttype,int pageIndex,int pageSize,HttpServletResponse response) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		//公共图片获取list
		if(act.equals(PUBLICPIC)){
			List<Public_pic> list = picService.list(parentid,parenttype);
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			
			map.put("total", list.size());
			map.put("data", list);
			String json = JSON.toJSONString(map);
			out.print(json);
		}
		//公共图片获取list 分页
		else if(act.equals(PUBLICPIC_PAGE)){
			PageBean<Public_pic> p = new PageBean<Public_pic>();
			p.setPageSize(pageSize);
			p.setPageNo(pageIndex + 1);
			p.setOrderBy("pic_sort");
			p.setOrderType("asc");
			p = picService.list(p,parentid,parenttype);
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			
			map.put("total", p.getRowCount());
			map.put("data", p.getList());
			String json = JSON.toJSONString(map);
			out.print(json);
		}
		//专题制作
		else if(act.equals(SUBJECT)){
			PageBean<Public_content> p = new PageBean<Public_content>();
			p.setPageSize(pageSize);
			p.setPageNo(pageIndex + 1);
			p.setOrderBy("sort");
			p.setOrderType("asc");
			p.setOrderBy("createtime");
			p.setOrderType("desc");
			p = contentService.list(p,parentid,parenttype);
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			
			map.put("total", p.getRowCount());
			map.put("data", p.getList());
			String json = JSON.toJSONString(map);
			out.print(json);
		}
		
	}
	
	/**
	 * 保存
	 * @param objs
	 * @param response
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public void save(String act,String parentid,String parenttype,String objs,HttpServletResponse response) throws IOException, IllegalAccessException, InvocationTargetException {
		
		response.setContentType("text/xml;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String result = "success";
		
		if ("".equals(objs) || objs == null) {
			out.print(result);
			return;
		}
		
		//
		List<Map<String, String>> rows = (List<Map<String, String>>) JSON.parse(objs);
		
		for(int i=0,l=rows.size(); i<l; i++){
			Map<String,String> row = (Map<String,String>)rows.get(i);
	  		  
			String id = row.get("id") != null ? row.get("id").toString() : "";
	        String state = row.get("_state") != null ? row.get("_state").toString() : "";
	        //新增：id为空，或_state为added
	        if(state.equals("added") || id.equals("")) {
	        	insert(row,act,parentid,parenttype);
	        }
	        else if (state.equals("removed") || state.equals("deleted")) {
	        	boolean flag = false;
	        	String filePath = "";
	        	if(act.equals(PUBLICPIC)){
	        		filePath = row.get("pic_path");
		        	flag = delete(id,act); //需要删除附件的，要有返回值
	        	}
	        	else if(act.equals(SUBJECT)){
	        		delete(id, act);
	        	}
	        	
	        	if(flag){
	        		FileOperate.delFile(super.getProjectRealPath() + filePath);
	        	}
	        }
	        //更新：_state为空，或modified
	        else if (state.equals("modified") || state.equals(""))	 {
	            update(row,act);
	        }
	    }
		
		out.print(result);
	}
	
	private boolean insert(Map<String,String> row,String act,String parentid,String parenttype) throws IllegalAccessException, InvocationTargetException {
		if (null == row) {
			return false;
		}
		if(act.equals(PUBLICPIC)){
			Public_pic pic = new Public_pic();
			BeanUtils.populate(pic, row);
			
			pic.setId(UUID.randomUUID().toString());
			pic.setParentid(parentid);
			pic.setParenttype(parenttype);
			pic = picService.insert(pic);
			return true;
		}
		else if(act.equals(SUBJECT)){
			Public_content content = new Public_content();
			BeanUtils.populate(content, row);
			
			content.setId(UUID.randomUUID().toString());
			content.setParentid(parentid);
			content.setParenttype(parenttype);
			String createtime = DateUtil.getNowTime("yyyy-MM-dd HH:mm:ss");
			content.setCreatetime(createtime);;
			content = contentService.insert(content);
			return true;
		}
		return false;
	}
	
	/**
	 * 删除。
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private boolean delete(String id,String act) throws IOException {
		if (id == null || id.equals("") ) {
			return false;
		}
		int num = 0;
		if(act.equals(PUBLICPIC)){
			num = picService.delete(id);
		}
		else if(act.equals(SUBJECT)){
			num = contentService.delete(id);
		}
		if (num <= 0 ) {
			return false;
		}
		return true;
	}
	/**
	 * 更新
	 * @param row
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private boolean update(Map<String,String> row,String act) throws IOException, IllegalAccessException, InvocationTargetException {
		
		if (null == row) {
			return false;
		}
		int num = 0;
		if(act.equals(PUBLICPIC)){
			Public_pic pic = new Public_pic();
			BeanUtils.populate(pic, row);
			num = picService.update(pic);
		}
		else if(act.equals(SUBJECT)){
			Public_content pic = new Public_content();
			BeanUtils.populate(pic, row);
			num = contentService.update(pic);
		}
		
		if (num <= 0 ) {
			return false;
		}
		return true;
	}
	
	/**
	 * 专为内容页富文本编辑器上传图片用。存储图片到文件夹，返回路径。
	 * @param file
	 * @param editor
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value="/upload_content_pic",method=RequestMethod.POST,produces = "text/html; charset=UTF-8")
	public String upload_content_pic(@RequestParam("file") MultipartFile file,String editor,HttpServletRequest request) throws IOException {
		String uuidString = "";
		String newName = "";
		String oldName = "";
		String newFilePath = "";
		String path = request.getSession().getServletContext().getRealPath("/file/richedit/");
		
        int size = file.getInputStream().available();
		if (size != 0) {
			
	        String ext = "";//扩展名
	        
	        oldName = file.getOriginalFilename();
	        //获取扩展名
	        if (oldName.lastIndexOf(".") >= 0) {
	            ext = oldName.substring(oldName.lastIndexOf("."));
	        }
	        
	        uuidString = UUID.randomUUID().toString();
	        newName = uuidString + ext;
	        newFilePath = path + "/" + newName;
	        File excFile = new File(newFilePath);
	        FileCopyUtils.copy(file.getBytes(),excFile);
		}
		String project_path = getProjectPath();
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("window.parent.").append(editor).append(".insertContent('<img src=\""+project_path+"/file/richedit/"+newName+"\"/>');");
		sb.append("window.parent."+editor+".plugins.upload.finish();");
		sb.append("</script>");
		return sb.toString();
        
	}
	
}
