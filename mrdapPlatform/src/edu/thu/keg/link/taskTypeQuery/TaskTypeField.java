package edu.thu.keg.link.taskTypeQuery;

public enum TaskTypeField
{
	
	 ID("id"),
	 NAME("name"),
	 JAR_NAME("jarName"),
	 CLASS_NAME("className"),
	 ARGS_NUM("argsNum"),
	 INPUT_DATA_TYPE("inputDataType"),
	 INPUTMETA("inputMeta"),
	 OUTPUT_NUM("outputNum"),
     OUTPUT_META("outputMeta"),
	 DESCRIPTION("description");
	 
	 private String info=null;
	 private TaskTypeField(String info)
	 {
		 this.info=info;
	 }
	 
	 @Override
	 public String toString()
	 {
		 return info;
	 }
	 
	
	 
	 /*<id>201408181319</id>
	  <name>Task1</name>
	  <jarName>Task1.jar</jarName>
	  <className>mobile.Task1</className>
	  <argsNum>0</argsNum>
	  <outputNum>1</outputNum>
	  <inputDataType>xxxxxx</inputDataType>
	  <inputMetaInfo>x1,x2,x3</inputMetaInfo>+
	  <outputMetaInfo>prefix:meta1;prefix2:meta2</outputMetaInfo>
	  <description>task1 test</description>*/
}
