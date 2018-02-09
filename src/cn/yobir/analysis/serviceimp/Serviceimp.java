package cn.yobir.analysis.serviceimp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bitagentur.chart.JChartLibLineChart;
import com.bitagentur.data.JChartLibDataSet;
import com.bitagentur.data.JChartLibSerie;
import com.bitagentur.renderer.JChartLibLinechartRenderer;

import cn.yobir.analysis.service.*;
import cn.yobir.ganalysis.dao.MissDao;
import cn.yobir.ganalysis.dao.NumberDao;
import cn.yobir.ganalysis.daoimp.MissDaoimp;
import cn.yobir.ganalysis.daoimp.NumberDaoimp;

public class Serviceimp implements Service {

	private static NumberDao numberdao = new NumberDaoimp();
	private static MissDao missdao = new MissDaoimp();

	@Override
	public void pullDataFromWebSerive() {
		class XmlParse {
			public Document parse(URL url) throws DocumentException {
				SAXReader sax = new SAXReader();
				Document doc = sax.read(url);
				return doc;

			}

			public String[] getDataFromDoc() throws MalformedURLException {
				URL url = null;
				Element root = null;
				String temp[] = new String[3];
				try {
					url = new URL("http://f.apiplus.net/fc3d-1.xml");
					root = parse(url).getRootElement();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
					Element element = it.next();
					for (Iterator<Attribute> it1 = element.attributeIterator(); it.hasNext();) {
						Attribute attribute = it1.next();
						if (attribute.getName().equals("expect")) {
							temp[0] = attribute.getValue();
						} else if (attribute.getName().equals("opencode")) {
							temp[1] = attribute.getValue();
						} else {
							temp[2] = attribute.getValue();
						}
					}
				}
				return temp;
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void storeToDatabase() throws SQLException {
				String data[] = null;
				try {
					data = getDataFromDoc();
				} catch (MalformedURLException e) {

					e.printStackTrace();
				}
				int x = Integer.parseInt((data[1].charAt(0)) + "");
				int y = Integer.parseInt((data[1].charAt(2)) + "");
				int z = Integer.parseInt((data[1].charAt(4)) + "");

				if (x == y || x == z || y == z) {
					Map map = new HashMap();
					map.put("mark", data[0]);
					map.put("numb", data[2]);
					map.put("date", data[4]);
					if (numberdao.getCount() == 0) {
						numberdao.add(map);
						map.clear();
						map.put("count", 0);
						missdao.add(map);

					} else {
						if (numberdao.findAsLimit(numberdao.getCount(), 1).get(0).get("mark") != map.get("mark")) {
							map.put("miss", missdao.findAll().get(0).get("count"));
							numberdao.add(map);
							if (numberdao.getCount() > 15) {
								List<Map> list = numberdao.findAsLimit(numberdao.getCount() - 14, 15);
								float a = 0;
								float b = 0;
								float c = 0;
								for (int i = 0; i < numberdao.getCount(); i++) {

									a += Integer.parseInt(list.get(i).get("miss").toString());
									if (i > 4) {
										b += Integer.parseInt(list.get(i).get("miss").toString());
									} else if (i > 9) {

										c += Integer.parseInt(list.get(i).get("miss").toString());

									}

								}
								a /= 15;
								b /= 10;
								a /= 15;
								NumberFormat format = NumberFormat.getNumberInstance();
								format.setMaximumFractionDigits(2);
								map.clear();
								map.put("avg15", format.format((Float.valueOf(a).toString())));
								map.put("avg10", format.format((Float.valueOf(b).toString())));
								map.put("avg5", format.format((Float.valueOf(c).toString())));
								numberdao.update(map);
							}

						}

					}

				} else {
					if (numberdao.getCount() != 0) {
						Map map = new HashMap();
						map.put("count", Integer.parseInt(missdao.findAll().get(0).get("count").toString()) + 1);

						missdao.update(map);
					}
				}
			}
		}
		try {
			new XmlParse().storeToDatabase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void MakeChart(int count) {
		class ChartGenerate {
			NumberDao numberdao = new NumberDaoimp();

			public JChartLibDataSet getDataSet(int start_index, int end_index) {
				JChartLibDataSet dataset = new JChartLibDataSet();
				JChartLibSerie serie0 = new JChartLibSerie("AVG5");
				JChartLibSerie serie1 = new JChartLibSerie("AVG10");
				JChartLibSerie serie2 = new JChartLibSerie("AVG15");

				List<Map> objs = null;
				try {
					objs = numberdao.findAsLimit(start_index, end_index);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				for (ListIterator<Map> lt = objs.listIterator(); lt.hasNext();) {
					Map obj = lt.next();
					serie0.addValue(Integer.parseInt((String) obj.get("avg5")));
					serie1.addValue(Integer.parseInt((String) obj.get("avg10")));
					serie2.addValue(Integer.parseInt((String) obj.get("avg15")));

				}
				dataset.addDataSerie(serie0);
				dataset.addDataSerie(serie1);
				dataset.addDataSerie(serie2);
				return dataset;

			}

			public void makeLineChart(int start_index, int end_index) {
				JChartLibDataSet dataset = getDataSet(start_index, end_index);
				JChartLibLineChart lineChart = new JChartLibLineChart("G3-ANALYSIS", "X-Axis", "Y-Axis", dataset);
				JChartLibLinechartRenderer renderer = (JChartLibLinechartRenderer) lineChart.getRender();
				lineChart.setRender(renderer);
				renderer.setDrawDayAndTime(true);
				renderer.setDrawdots(true);
				renderer.setShowminmax(true);

				List<Map> objs = null;
				try {
					objs = numberdao.findAsLimit(start_index, end_index);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (ListIterator<Map> lt = objs.listIterator(); lt.hasNext();) {
					Map obj = lt.next();
					renderer.addXAxisText(obj.get("mark").toString());

				}
				try {
					lineChart.saveAsJPEG("lineChat.jpeg", objs.size() * 20, 700);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
