/**
 * <P>Description: 乐视贵金属行情项目 - 利用爬虫程序从上海黄金交易所 https://www.sge.com.cn/ 抓历史日K数据. </P>
 * <P> 包内文件effectIndex.txt --> 上海黄金交易所网站上历史行情数据页面是这样的格式：
 * https://www.sge.com.cn/sjzx/mrhqsj/510778?top=789398439266459648 ，其中"510778"就是区别于不同日K页面的编码，
 * 但这个编码并不是连续的，所以先使用穷举方法将有效页面列出在effectIndex.txt中，之后调试重试直接从文件中读取
 * 有效编码，可以大大提升效率。</P>
 * <P> 包内文件dayK.txt --> 从页面上解析出来的日K数据文件 </P>
 * <P> 包内文件[dayK/monthK/weekK]InsertSql.txt --> 分析从页面上解析出的日K数据生成贵金属行情项目所需的
 * 日K、周K、月K，的插入数据库sql语句。 </P>
 *
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/8/23</P>
 * <P>UPDATE DATE: 2017/8/23</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
package com.xiafei.tools.spider.metalquotation;
