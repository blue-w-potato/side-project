package com.scriptauto.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScriptDao_Impl implements ScriptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScriptEntity> __insertionAdapterOfScriptEntity;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public ScriptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScriptEntity = new EntityInsertionAdapter<ScriptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scripts` (`id`,`name`,`orientation`,`componentsJson`,`createdAtEpochMillis`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScriptEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __converters.fromOrientation(entity.getOrientation());
        statement.bindString(3, _tmp);
        statement.bindString(4, entity.getComponentsJson());
        statement.bindLong(5, entity.getCreatedAtEpochMillis());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scripts WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final ScriptEntity entity, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfScriptEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScriptEntity>> observeAll() {
    final String _sql = "SELECT * FROM scripts ORDER BY createdAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scripts"}, new Callable<List<ScriptEntity>>() {
      @Override
      @NonNull
      public List<ScriptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfOrientation = CursorUtil.getColumnIndexOrThrow(_cursor, "orientation");
          final int _cursorIndexOfComponentsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "componentsJson");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final List<ScriptEntity> _result = new ArrayList<ScriptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScriptEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ScriptOrientation _tmpOrientation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfOrientation);
            _tmpOrientation = __converters.toOrientation(_tmp);
            final String _tmpComponentsJson;
            _tmpComponentsJson = _cursor.getString(_cursorIndexOfComponentsJson);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _item = new ScriptEntity(_tmpId,_tmpName,_tmpOrientation,_tmpComponentsJson,_tmpCreatedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllOnce(final Continuation<? super List<ScriptEntity>> $completion) {
    final String _sql = "SELECT * FROM scripts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScriptEntity>>() {
      @Override
      @NonNull
      public List<ScriptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfOrientation = CursorUtil.getColumnIndexOrThrow(_cursor, "orientation");
          final int _cursorIndexOfComponentsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "componentsJson");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final List<ScriptEntity> _result = new ArrayList<ScriptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScriptEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ScriptOrientation _tmpOrientation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfOrientation);
            _tmpOrientation = __converters.toOrientation(_tmp);
            final String _tmpComponentsJson;
            _tmpComponentsJson = _cursor.getString(_cursorIndexOfComponentsJson);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _item = new ScriptEntity(_tmpId,_tmpName,_tmpOrientation,_tmpComponentsJson,_tmpCreatedAtEpochMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super ScriptEntity> $completion) {
    final String _sql = "SELECT * FROM scripts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScriptEntity>() {
      @Override
      @Nullable
      public ScriptEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfOrientation = CursorUtil.getColumnIndexOrThrow(_cursor, "orientation");
          final int _cursorIndexOfComponentsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "componentsJson");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final ScriptEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ScriptOrientation _tmpOrientation;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfOrientation);
            _tmpOrientation = __converters.toOrientation(_tmp);
            final String _tmpComponentsJson;
            _tmpComponentsJson = _cursor.getString(_cursorIndexOfComponentsJson);
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            _result = new ScriptEntity(_tmpId,_tmpName,_tmpOrientation,_tmpComponentsJson,_tmpCreatedAtEpochMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
