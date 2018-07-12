import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class Solrj_index_crud {

	public static void main(String[] args){
		//添加或更新索引   如果id值来判断进行添加还是更新
		//addOrUpdate();
		
		//通过solrj进行索引的删除
		//delete();
		
		//通过solrj进行全文检索
		search();

	}

	private static void search() {
		try {
			//创建solrj客户端对象，通过该对象可以与solr应用交互      内部类
			HttpSolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/core1")
					.withConnectionTimeout(10000).build();
			
			//通过SolrQuery实例，可以设置查询的关键字  设置fl dl  高亮信息、分页等等
			SolrQuery solrQuery = new SolrQuery();
			//设置查询关键字
			solrQuery.setQuery("title:华为白色");
			//设置默认域
			//solrQuery.set("df", "title");
			//结果字段
			solrQuery.set("fl", "id,title");
			
			//设置分页查询
			solrQuery.setStart(0);
			solrQuery.setRows(2);//显示几条
			
			//设置过滤条件
			//solrQuery.set("fq", val);
			
			//打开高亮开关
			solrQuery.setHighlight(true);
			//指定需要高亮的字段
			solrQuery.addHighlightField("title");
			solrQuery.setHighlightSimplePre("<font color='red'>");
			solrQuery.setHighlightSimplePost("</font>");
			
			//将solrQuery传入client进行查询
			QueryResponse queryResponse= client.query(solrQuery);
			//获取普通结果集
			SolrDocumentList solrDocumentList = queryResponse.getResults();
			//获取得分最高的分数
			Float fl = solrDocumentList.getMaxScore();
			System.out.println("匹配度最高的："+fl);
			//获取总记录数
			long num = solrDocumentList.getNumFound();
			System.out.println("num:"+num);
			//获取高亮后的结果集
			Map<String,Map<String,List<String>>> highlighting = queryResponse.getHighlighting();
			
			/**
			 * 最外层的map集合    key:存放记录的id    value:文档的信息
			 * 里层的map集合        key:字段（id,titale） value:字段对应的值
			 */
			
			//获取每一条记录
			for(SolrDocument temp:solrDocumentList) {
				/*System.out.println("id:"+temp.get("id")+"\title:"+temp.get("title"));
				Map<String,List<String>> map = hig*/
				String id = (String) temp.get("id");
				String title = (String) temp.get("title");
				//高亮前的效果
				System.out.println("id:"+id+" title:"+title);
				
				//开始获取高亮后的数据
				Map<String,List<String>> docMaps = highlighting.get(id);
				String highTitle = docMaps.get("title").get(0);
				
				System.out.println(" title:"+highTitle);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	private static void delete() {
		try {
			//创建solrj客户端对象，通过该对象可以与solr应用交互      内部类
			HttpSolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/core1")
					.withConnectionTimeout(10000).build();
			client.deleteById("100");
			client.commit();
			//关闭资源
			client.close();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//添加或更新索引   如果id值来判断进行添加还是更新
	private static void addOrUpdate(){
		
		try {
			//创建solrj客户端对象，通过该对象可以与solr应用交互      内部类
			HttpSolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/core1")
					.withConnectionTimeout(10000).build();
			
			//创建文档实例，一个文档实例代表一条记录
			SolrInputDocument doc = new SolrInputDocument();
			
			//通过SolrInputDocument制定列以及值得信息
			//通过UUID生成id值
			String id = UUID.randomUUID().toString();
			doc.addField("id", id);
			doc.addField("title", "苹果7plus");
			
			client.add(doc);
			//提交事务
			client.commit();
			//关闭资源
			client.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
