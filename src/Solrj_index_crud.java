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
		//��ӻ��������   ���idֵ���жϽ�����ӻ��Ǹ���
		//addOrUpdate();
		
		//ͨ��solrj����������ɾ��
		//delete();
		
		//ͨ��solrj����ȫ�ļ���
		search();

	}

	private static void search() {
		try {
			//����solrj�ͻ��˶���ͨ���ö��������solrӦ�ý���      �ڲ���
			HttpSolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/core1")
					.withConnectionTimeout(10000).build();
			
			//ͨ��SolrQueryʵ�����������ò�ѯ�Ĺؼ���  ����fl dl  ������Ϣ����ҳ�ȵ�
			SolrQuery solrQuery = new SolrQuery();
			//���ò�ѯ�ؼ���
			solrQuery.setQuery("title:��Ϊ��ɫ");
			//����Ĭ����
			//solrQuery.set("df", "title");
			//����ֶ�
			solrQuery.set("fl", "id,title");
			
			//���÷�ҳ��ѯ
			solrQuery.setStart(0);
			solrQuery.setRows(2);//��ʾ����
			
			//���ù�������
			//solrQuery.set("fq", val);
			
			//�򿪸�������
			solrQuery.setHighlight(true);
			//ָ����Ҫ�������ֶ�
			solrQuery.addHighlightField("title");
			solrQuery.setHighlightSimplePre("<font color='red'>");
			solrQuery.setHighlightSimplePost("</font>");
			
			//��solrQuery����client���в�ѯ
			QueryResponse queryResponse= client.query(solrQuery);
			//��ȡ��ͨ�����
			SolrDocumentList solrDocumentList = queryResponse.getResults();
			//��ȡ�÷���ߵķ���
			Float fl = solrDocumentList.getMaxScore();
			System.out.println("ƥ�����ߵģ�"+fl);
			//��ȡ�ܼ�¼��
			long num = solrDocumentList.getNumFound();
			System.out.println("num:"+num);
			//��ȡ������Ľ����
			Map<String,Map<String,List<String>>> highlighting = queryResponse.getHighlighting();
			
			/**
			 * ������map����    key:��ż�¼��id    value:�ĵ�����Ϣ
			 * ����map����        key:�ֶΣ�id,titale�� value:�ֶζ�Ӧ��ֵ
			 */
			
			//��ȡÿһ����¼
			for(SolrDocument temp:solrDocumentList) {
				/*System.out.println("id:"+temp.get("id")+"\title:"+temp.get("title"));
				Map<String,List<String>> map = hig*/
				String id = (String) temp.get("id");
				String title = (String) temp.get("title");
				//����ǰ��Ч��
				System.out.println("id:"+id+" title:"+title);
				
				//��ʼ��ȡ�����������
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
			//����solrj�ͻ��˶���ͨ���ö��������solrӦ�ý���      �ڲ���
			HttpSolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/core1")
					.withConnectionTimeout(10000).build();
			client.deleteById("100");
			client.commit();
			//�ر���Դ
			client.close();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//��ӻ��������   ���idֵ���жϽ�����ӻ��Ǹ���
	private static void addOrUpdate(){
		
		try {
			//����solrj�ͻ��˶���ͨ���ö��������solrӦ�ý���      �ڲ���
			HttpSolrClient client = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/core1")
					.withConnectionTimeout(10000).build();
			
			//�����ĵ�ʵ����һ���ĵ�ʵ������һ����¼
			SolrInputDocument doc = new SolrInputDocument();
			
			//ͨ��SolrInputDocument�ƶ����Լ�ֵ����Ϣ
			//ͨ��UUID����idֵ
			String id = UUID.randomUUID().toString();
			doc.addField("id", id);
			doc.addField("title", "ƻ��7plus");
			
			client.add(doc);
			//�ύ����
			client.commit();
			//�ر���Դ
			client.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
