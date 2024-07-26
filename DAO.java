package com.example.tasktide.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tasktide.Objetos.Evento;
import com.example.tasktide.Objetos.Identidade;
import com.example.tasktide.Objetos.Informacoes;
import com.example.tasktide.Objetos.Participantes;

public class DAO extends SQLiteOpenHelper {

    private static final String TAG = "DAO";

    private static final String NOME_BANCO = "tasktide_db";
    private static final int VERSAO_BANCO = 4;

    private static final String TABELA_EVENTO = "evento";
    private static final String TABELA_INFORMACOES = "informacoes";
    private static final String TABELA_IDENTIDADE = "identidade";
    private static final String TABELA_PARTICIPANTES = "participantes";



    public DAO(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlEvento = "CREATE TABLE " + TABELA_EVENTO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nomeEvento TEXT," +
                "tipoEvento TEXT," +
                "horasComplementares TEXT," +
                "modalidade TEXT)";
        db.execSQL(sqlEvento);
        Log.i(TAG, "Tabela evento criada com sucesso. Local: " + db.getPath());

        createInformacoesTable(db);
        createIdentidadeTable(db);
        createParticipantesTable(db);

    }

    private void createInformacoesTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_INFORMACOES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_evento INTEGER," +
                "dataPrevis TEXT," +
                "horarioInicio TEXT," +
                "horarioTermino TEXT," +
                "local TEXT," +
                "FOREIGN KEY (id_evento) REFERENCES " + TABELA_EVENTO + "(id))";
        db.execSQL(sql);
        Log.i(TAG, "Tabela informacoes criada com sucesso.");
    }

    private void createIdentidadeTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_IDENTIDADE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_evento INTEGER," +
                "nome TEXT," +
                "cargo TEXT," +
                "departamento TEXT," +
                "contato TEXT," +
                "FOREIGN KEY (id_evento) REFERENCES " + TABELA_EVENTO + "(id))";
        db.execSQL(sql);
        Log.i(TAG, "Tabela identidade criada com sucesso.");
    }

    private void createParticipantesTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_PARTICIPANTES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_evento INTEGER," +
                "quantParticipantes TEXT," +
                "FOREIGN KEY (id_evento) REFERENCES " + TABELA_EVENTO + "(id))";
        db.execSQL(sql);
        Log.i(TAG, "Tabela participantes criada com sucesso.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_PARTICIPANTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_INFORMACOES);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_IDENTIDADE);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_EVENTO);
            onCreate(db);
            Log.i(TAG, "Tabelas atualizadas para a nova versão do banco de dados.");
        }
    }

    public long inserirEvento(Evento evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomeEvento", evento.getNomeEvento());
        values.put("tipoEvento", evento.getTipoEvento());
        values.put("horasComplementares", evento.getHorasComplementares());
        values.put("modalidade", evento.getModalidade());

        long id = db.insert(TABELA_EVENTO, null, values);
        db.close();
        if (id != -1) {
            Log.i(TAG, "Evento inserido com sucesso. ID: " + id);
        } else {
            Log.e(TAG, "Erro ao inserir evento.");
        }
        return id;
    }

    public long inserirIdentidade(Identidade identidade, long id_evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_evento", id_evento);
        values.put("nome", identidade.getNome());
        values.put("cargo", identidade.getCargo());
        values.put("departamento", identidade.getDepartamento());
        values.put("contato", identidade.getContato());

        long id = db.insert(TABELA_IDENTIDADE, null, values);
        db.close();
        if (id != -1) {
            Log.i(TAG, "Identidade inserida com sucesso. ID: " + id);
        } else {
            Log.e(TAG, "Erro ao inserir identidade.");
        }
        return id;
    }

    public long inserirInformacoes(Informacoes informacoes, long id_evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_evento", id_evento);
        values.put("dataPrevis", informacoes.getDataPrevis());
        values.put("horarioInicio", informacoes.getHorarioInicio());
        values.put("horarioTermino", informacoes.getHorarioTermino());
        values.put("local", informacoes.getLocal());

        long id = db.insert(TABELA_INFORMACOES, null, values);
        db.close();
        if (id != -1) {
            Log.i(TAG, "Informações inseridas com sucesso. ID: " + id);
        } else {
            Log.e(TAG, "Erro ao inserir informações.");
        }
        return id;
    }

    public long inserirParticipantes(Participantes participantes, long id_evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantParticipantes", participantes.getQuantParticipantes());
        values.put("id_evento", id_evento);  // Associar ao evento específico

        long id = db.insert(TABELA_PARTICIPANTES, null, values);
        db.close();
        if (id != -1) {
            Log.i(TAG, "Participantes inseridos com sucesso. ID: " + id);
        } else {
            Log.e(TAG, "Erro ao inserir participantes.");
        }
        return id;
    }

    //fases de teste, yupi
    //novo
    public Evento getEventoById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Evento evento = null;
        Cursor cursor = db.query(TABELA_EVENTO,
                new String[]{"nomeEvento", "tipoEvento", "horasComplementares"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            evento = new Evento();
            evento.setNomeEvento(cursor.getString(cursor.getColumnIndexOrThrow("nomeEvento")));
            evento.setTipoEvento(cursor.getString(cursor.getColumnIndexOrThrow("tipoEvento")));
            evento.setHorasComplementares(cursor.getString(cursor.getColumnIndexOrThrow("horasComplementares")));
            cursor.close();
        }
        db.close();
        return evento;
    }

    //novo
    //sem funcionamento(por enquanto)
    public void LimparTabelas() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_PARTICIPANTES, null, null);
        db.delete(TABELA_INFORMACOES, null, null);
        db.delete(TABELA_IDENTIDADE, null, null);
        db.delete(TABELA_EVENTO, null, null);
        Log.i(TAG, "Todas as tabelas foram limpas.");
        db.close();
    }



}
