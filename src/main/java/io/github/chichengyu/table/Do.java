//package io.github.chichengyu.table;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 类的信息
// *
// * author xiaochi
// * Date 2022/11/12
// */
//public class Do {
//
//    private String poName;
//
//    private String poRemark;
//
//    private List<Param> params = new ArrayList<>();
//
//    public String getPoName() {
//        return poName;
//    }
//
//    public void setPoName(String poName) {
//        this.poName = poName;
//    }
//
//    public String getPoRemark() {
//        return poRemark;
//    }
//
//    public void setPoRemark(String poRemark) {
//        this.poRemark = poRemark;
//    }
//
//    public List<Param> getParams() {
//        return params;
//    }
//
//    public void setParams(List<Param> params) {
//        this.params = params;
//    }
//
//    class Param {
//        private String paramName;
//
//        private String paramType;
//
//        private String paramDefaultValue;
//
//        private String paramRemark;
//
//        public String getParamName() {
//            return paramName;
//        }
//
//        public void setParamName(String paramName) {
//            this.paramName = paramName;
//        }
//
//        public String getParamType() {
//            return paramType;
//        }
//
//        public void setParamType(String paramType) {
//            this.paramType = paramType;
//        }
//
//        public String getParamDefaultValue() {
//            return paramDefaultValue;
//        }
//
//        public void setParamDefaultValue(String paramDefaultValue) {
//            this.paramDefaultValue = paramDefaultValue;
//        }
//
//        public String getParamRemark() {
//            return paramRemark;
//        }
//
//        public void setParamRemark(String paramRemark) {
//            this.paramRemark = paramRemark;
//        }
//
//        @Override
//        public String toString() {
//            return "Param{" +
//                    "paramName='" + paramName + '\'' +
//                    ", paramType='" + paramType + '\'' +
//                    ", paramDefaultValue='" + paramDefaultValue + '\'' +
//                    ", paramRemark='" + paramRemark + '\'' +
//                    '}';
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "Do{" +
//                "poName='" + poName + '\'' +
//                ", poRemark='" + poRemark + '\'' +
//                ", params=" + params +
//                '}';
//    }
//}
