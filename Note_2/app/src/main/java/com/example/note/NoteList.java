package com.example.note;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class NoteList extends ListActivity {
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
	
    private static final int DELETE_ID = Menu.FIRST;
	private int mNoteNumber = 1;
	
	private NotesDbAdapter mDbHelper;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);
		mDbHelper = new NotesDbAdapter (this);
		mDbHelper.open();
		fillData();				
		registerForContextMenu(getListView());
		Button addnote = (Button)findViewById(R.id.addnotebutton);
		addnote.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				createNote();
				}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notelist_menu, menu);
		return true;		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_about:
	          
	           AlertDialog.Builder dialog = new AlertDialog.Builder(NoteList.this);
	           dialog.setMessage("Welcome!");
	           dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
	        	   @Override
	        	   public void onClick(DialogInterface dialog, int which) {
	        		   dialog.cancel();
					
	        	   }
	           });
	           dialog.show();	           
	           return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	
	private void createNote() {
		Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);    	
    }
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

	private void fillData() {

        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);
        

        String[] from = new String[] { NotesDbAdapter.KEY_TITLE ,NotesDbAdapter.KEY_DATE};
        int[] to = new int[] { R.id.text1 ,R.id.date_row};

        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
    }
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();        
    }   
    
}
