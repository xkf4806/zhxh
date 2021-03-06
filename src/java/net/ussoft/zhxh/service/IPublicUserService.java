package net.ussoft.zhxh.service;

import java.util.List;
import java.util.Map;

import net.ussoft.zhxh.model.PageBean;
import net.ussoft.zhxh.model.Public_user;

/**
 * 用户管理
 * @author guodh
 * @version v1.0 2016.07.27
 * */
public interface IPublicUserService {

	/**
	 * 根据ID获取对象
	 * @param id
	 * @return Public_user
	 * */
	public Public_user getById(String id);
	
	/**
	 * 根据手机号获取对象
	 * @return list
	 * */
	public Public_user getByPhoneNum(String phoneNum);
	
	/**
	 * 查询所有
	 * @return list
	 * */
	public List<Public_user> list();
	
	/**
	 * 获取个人中心数据。
	 * @param parentid			上级id
	 * @param userid			为谁设置推荐人 ，要排除掉这个人的id
	 * @param identity			级别代码
	 * @param map				查询条件
	 * @param showtype			显示结果类型。1，正常的。0：待关联的
	 * @param pageBean			
	 * @return
	 */
	public PageBean<Public_user> list(String parentid,String identity,Map<String, Object> map,int showtype,PageBean<Public_user> pageBean);
	
	/**
	 * 待关联机构
	 * @param pageBean
	 * @return
	 * */
	public PageBean<Public_user> noParentList(PageBean<Public_user> pageBean);
	
	/**
	 * 获取设置推荐人列表
	 * @param parentid
	 * @param identity
	 * @param map
	 * @param pageBean
	 * @return
	 */
	public PageBean<Public_user> setTuijianList(String parentid,String userid,String identity,Map<String, Object> map,PageBean<Public_user> pageBean);
	/**
	 * 获取推荐客户列表
	 * @param parentid
	 * @param pageBean
	 * @return
	 */
	public PageBean<Public_user> tuijianList(String parentid,PageBean<Public_user> pageBean);
	
	/**
	 * 获取机构的上级列表
	 * @param userid
	 * @param map
	 * @param pageBean
	 * @return
	 */
	public PageBean<Public_user> getParentlist(String userid,Map<String, Object> map,PageBean<Public_user> pageBean);
	
	/**
	 * 获取机构的父机构列表
	 * @param userid
	 * @return
	 */
	public List<Public_user> listParent(String userid);
	
	
	/**
	 * 个人中心关联关系查询
	 * @param parentid
	 * @param pageBean
	 * @return
	 * */
	public PageBean<Public_user> list(String parentid,String identity,PageBean<Public_user> pageBean);
	
	/**
	 * 条件查询 分页
	 * */
	public PageBean<Public_user> list(Map<String, Object> map,PageBean<Public_user> pageBean);
	
	/**
	 * 查询所有店
	 * @param blnshop true:直营店；false:非直营店
	 * @param pageBean
	 * @return list
	 * */
	public PageBean<Public_user> list_shop(boolean blnshop,PageBean<Public_user> pageBean);
	
	/**
	 * 添加 - 代理、店
	 * @param Public_user
	 * @param parentid
	 * @return 
	 * */
	public int insert(Public_user user);
	
	/**
	 * 修改
	 * @param Public_user
	 * @return 
	 * */
	public int update(Public_user user);
	
	/**
	 * 初始化机构密码
	 * @param id
	 */
	public void initUpdatePass(String id);
	
	/**
	 * 解除关联关系
	 * @param parentid
	 * @param userids
	 */
	public void outLink(String parentid,String userids);
	
	/**
	 * 修改其上级机构
	 * @param userid
	 * @param oldparentid
	 * @param newparentid
	 * @return 
	 * */
	public int userMove(String userids,String oldParentid,String newParentid);
	
	/**
	 * 代理添加店-创建关联关系
	 * @param userid
	 * @param parentid
	 * @return
	 * */
	public int createlink(String parentid,String userids);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 * */
	public int delete(String id);
	
	/**
	 * 检查店是否能删除。主要是为代理管理页面，店没有显示包含的普通会员，所以要ajax判断
	 * @param ids
	 * @return
	 */
	public String checkDel(String ids);
	
	/**
	 * 验证手机号是否已存在
	 * @param phoneNum
	 * @return true:存在；false:不存在
	 * */
	public boolean checkPhoneNum(String phoneNum);
	
	/**
	 * 登录验证密码
	 * @param userame
	 * @param password
	 * */
	public Public_user login(String username,String password);
	
	/**
	 * 批量修改机构信息
	 * @param updateUserids
	 * @param field
	 * @param fieldValue
	 * @return
	 */
	public boolean updateBatch(String parentid,String updateUserids,String field,String fieldValue);
	
}
