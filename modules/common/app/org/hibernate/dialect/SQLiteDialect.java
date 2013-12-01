/*
 * The author disclaims copyright to this source code.  In place of
 * a legal notice, here is a blessing:
 *
 *    May you do good and not evil.
 *    May you find forgiveness for yourself and forgive others.
 *    May you share freely, never taking more than you give.
 *
 */
package org.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.function.AbstractAnsiTrimEmulationFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class SQLiteDialect extends Dialect {

	public SQLiteDialect() {
		this.registerColumnType(Types.BIT, "boolean");
		this.registerColumnType(Types.TINYINT, "tinyint");
		this.registerColumnType(Types.SMALLINT, "smallint");
		this.registerColumnType(Types.INTEGER, "integer");
		this.registerColumnType(Types.BIGINT, "bigint");
		this.registerColumnType(Types.FLOAT, "float");
		this.registerColumnType(Types.REAL, "real");
		this.registerColumnType(Types.DOUBLE, "double");
		this.registerColumnType(Types.NUMERIC, "numeric($p, $s)");
		this.registerColumnType(Types.DECIMAL, "decimal");
		this.registerColumnType(Types.CHAR, "char");
		this.registerColumnType(Types.VARCHAR, "varchar($l)");
		this.registerColumnType(Types.LONGVARCHAR, "longvarchar");
		this.registerColumnType(Types.DATE, "date");
		this.registerColumnType(Types.TIME, "time");
		this.registerColumnType(Types.TIMESTAMP, "datetime");
		this.registerColumnType(Types.BINARY, "blob");
		this.registerColumnType(Types.VARBINARY, "blob");
		this.registerColumnType(Types.LONGVARBINARY, "blob");
		this.registerColumnType(Types.BLOB, "blob");
		this.registerColumnType(Types.CLOB, "clob");
		this.registerColumnType(Types.BOOLEAN, "boolean");
		this.registerColumnType(Types.NULL, "null");

		this.registerHibernateType(Types.NULL, "null");

		//registerFunction( "abs", new StandardSQLFunction("abs") );
		this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
		//registerFunction( "length", new StandardSQLFunction("length", StandardBasicTypes.LONG) );
		//registerFunction( "lower", new StandardSQLFunction("lower") );
		this.registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 % ?2"));
		this.registerFunction("quote", new StandardSQLFunction("quote", StandardBasicTypes.STRING));
		this.registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.INTEGER));
		this.registerFunction("round", new StandardSQLFunction("round"));
		this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
		this.registerFunction("substring", new SQLFunctionTemplate(StandardBasicTypes.STRING, "substr(?1, ?2, ?3)"));
		this.registerFunction("trim", new AbstractAnsiTrimEmulationFunction() {
			@Override
			protected SQLFunction resolveBothSpaceTrimFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1)");
			}

			@Override
			protected SQLFunction resolveBothSpaceTrimFromFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?2)");
			}

			@Override
			protected SQLFunction resolveLeadingSpaceTrimFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(?1)");
			}

			@Override
			protected SQLFunction resolveTrailingSpaceTrimFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(?1)");
			}

			@Override
			protected SQLFunction resolveBothTrimFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1, ?2)");
			}

			@Override
			protected SQLFunction resolveLeadingTrimFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(?1, ?2)");
			}

			@Override
			protected SQLFunction resolveTrailingTrimFunction() {
				return new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(?1, ?2)");
			}
		});
		//registerFunction( "upper", new StandardSQLFunction("upper") );
	}

	@Override
	public boolean supportsIdentityColumns() {
		return true;
	}

	/*
	 * public boolean supportsInsertSelectIdentity() { return true; // As
	 * specify in NHibernate dialect }
	 */

	@Override
	public boolean hasDataTypeInIdentityColumn() {
		return false; // As specify in NHibernate dialect
	}

	/*
	 * public String appendIdentitySelectToInsert(String insertString) { return
	 * new StringBuffer(insertString.length()+30). // As specify in NHibernate
	 * dialect append(insertString).
	 * append("; ").append(getIdentitySelectString()). toString(); }
	 */

	@Override
	public String getIdentityColumnString() {
		// return "integer primary key autoincrement";
		return "integer";
	}

	@Override
	public String getIdentitySelectString() {
		return "select last_insert_rowid()";
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}

	@Override
	protected String getLimitString(String query, boolean hasOffset) {
		return new StringBuffer(query.length() + 20).append(query).append(hasOffset ? " limit ? offset ?" : " limit ?").toString();
	}

	@Override
	public boolean supportsTemporaryTables() {
		return true;
	}

	@Override
	public String getCreateTemporaryTableString() {
		return "create temporary table if not exists";
	}

	@Override
	public boolean dropTemporaryTableAfterUse() {
		return true; // TODO Validate
	}

	@Override
	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	@Override
	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	@Override
	public String getCurrentTimestampSelectString() {
		return "select current_timestamp";
	}

	@Override
	public boolean supportsUnionAll() {
		return true;
	}

	@Override
	public boolean hasAlterTable() {
		return false; // As specify in NHibernate dialect
	}

	@Override
	public boolean dropConstraints() {
		return false;
	}

	/*
	 * public String getAddColumnString() { return "add column"; }
	 */

	@Override
	public String getForUpdateString() {
		return "";
	}

	@Override
	public boolean supportsOuterJoinForUpdate() {
		return false;
	}

	@Override
	public String getDropForeignKeyString() {
		throw new UnsupportedOperationException("No drop foreign key syntax supported by SQLiteDialect");
	}

	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
		throw new UnsupportedOperationException("No add foreign key syntax supported by SQLiteDialect");
	}

	@Override
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		throw new UnsupportedOperationException("No add primary key syntax supported by SQLiteDialect");
	}

	@Override
	public boolean supportsIfExistsBeforeTableName() {
		return true;
	}

	@Override
	public boolean supportsCascadeDelete() {
		return true;
	}

	/*
	 * not case insensitive for unicode characters by default (ICU extension
	 * needed) public boolean supportsCaseInsensitiveLike() { return true; }
	 */

	@Override
	public boolean supportsTupleDistinctCounts() {
		return false;
	}

	@Override
	public String getSelectGUIDString() {
		return "select hex(randomblob(16))";
	}
}