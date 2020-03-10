package android.trc.com.trdevapp.config;

import java.util.List;

/**
 * JiangyeLin on 2018/6/6
 */
public class RemoteModel {

    public int code;
    public String message;
    public List<ContentBean> content;

    public static class ContentBean {
        /**
         * _id : 5b16441cc1bd083e0d430dc4
         * name : 正式环境
         * type : production
         * platform : cube
         * terminal : android
         * editable : false
         * deletable : false
         * configList : {"api":"https://mofang.tfabric.com"}
         * __v : 0
         */

        public String _id;
        public String name;
        public String type;
        public String platform;
        public String terminal;
        public boolean editable;    //是否可编辑 (除生产环境外，其余都可编辑)
        public boolean deletable;   //是否可删除 (只有自定义环境可删除)
        public Object configList;   //api配置信息列表
        public int __v;

    }
}
