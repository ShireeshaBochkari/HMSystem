// ---- rooms ----
async function roomsView(){
  $('#app').innerHTML=nav()+`<main class="card"><h3>Rooms</h3>${isAdmin()?`<form class="bar" id="rf" novalidate><input name="roomNo" placeholder="Room no. e.g. 104" style="max-width:180px"><input name="capacity" type="number" placeholder="Capacity" style="max-width:120px"><button>Add room</button></form><small class="err" id="ferr"></small>`:''}
  <div class="wrap" id="tbl"></div></main>`;
  if(isAdmin())$('#rf').onsubmit=async e=>{e.preventDefault();const d=Object.fromEntries(new FormData(e.target));
    if(!d.roomNo.trim()||!(+d.capacity>=1&&+d.capacity<=10))return showErrors({},'Enter a room number and capacity (1-10)');
    try{await api('/rooms',{method:'POST',body:JSON.stringify({roomNo:d.roomNo,capacity:+d.capacity})});toast('Room added');roomsView()}catch(x){showErrors(x.errors||{},x.message)}};
  const t=$('#tbl');t.innerHTML='<div class="msg">Loading…</div>';
  try{rooms=await api('/rooms');
    t.innerHTML=rooms.length?'<table><tr><th>Room</th><th>Capacity</th><th>Occupied</th>'+(isAdmin()?'<th></th>':'')+'</tr>'+
      rooms.map(r=>`<tr><td>${esc(r.roomNo)}</td><td>${r.capacity}</td><td>${r.occupied}</td>${isAdmin()?`<td><button class="d" onclick="delRoom(${r.id})">Delete</button></td>`:''}</tr>`).join('')+'</table>':'<div class="msg">No rooms yet.</div>'}
  catch(e){t.innerHTML=`<div class="msg">${esc(e.message)}<br><br><button onclick="roomsView()">Retry</button></div>`}}
async function delRoom(id){if(!confirm('Delete this room?'))return;
  try{await api('/rooms/'+id,{method:'DELETE'});toast('Room deleted');roomsView()}catch(x){toast(x.message,true)}}
