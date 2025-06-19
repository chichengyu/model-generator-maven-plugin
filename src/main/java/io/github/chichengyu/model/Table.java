package io.github.chichengyu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 表的信息
 * author xiaochi
 * Date 2022/11/12
 */
public class Table {

	// 表名
	private String tableName;

	// 备注
	private String tableRemark;

	// 列
	private List<Column> columns = new ArrayList<>();
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableRemark() {
		return tableRemark;
	}

	public void setTableRemark(String tableRemark) {
		this.tableRemark = tableRemark;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public static class Column {
		// 名称
		private String columnName;
		// 类型
		private String columnType;
		// 长度
		private String columnSize;
		// 是否可为空
		private Integer columnNullable;
		// 默认值
		private String columnDefaultValue;
		// 备注
		private String columnRemark;

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnType() {
			return columnType;
		}

		public void setColumnType(String columnType) {
			this.columnType = columnType;
		}

		public String getColumnSize() {
			return columnSize;
		}

		public void setColumnSize(String columnSize) {
			this.columnSize = columnSize;
		}

		public Integer getColumnNullable() {
			return columnNullable;
		}

		public void setColumnNullable(Integer columnNullable) {
			this.columnNullable = columnNullable;
		}

		public String getColumnDefaultValue() {
			return columnDefaultValue;
		}

		public void setColumnDefaultValue(String columnDefaultValue) {
			this.columnDefaultValue = columnDefaultValue;
		}

		public String getColumnRemark() {
			return columnRemark;
		}

		public void setColumnRemark(String columnRemark) {
			this.columnRemark = columnRemark;
		}

		@Override
		public String toString() {
			return "Column{" +
					"columnName='" + columnName + '\'' +
					", columnType='" + columnType + '\'' +
					", columnSize=" + columnSize +
					", columnNullable=" + columnNullable +
					", columnDefaultValue='" + columnDefaultValue + '\'' +
					", columnRemark='" + columnRemark + '\'' +
					'}';
		}

	}

	@Override
	public String toString() {
		return "Table{" +
				"tableName='" + tableName + '\'' +
				", tableRemark='" + tableRemark + '\'' +
				", columns=" + columns +
				'}';
	}
}
