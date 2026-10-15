// ---- students ----
async function studentsView(){
  $('#app').innerHTML=nav()+`<main class="card"><div class="bar"><input id="q" placeholder="Search by name" value="${esc(st.q)}">
  <select id="sort"><option value="id">Newest first</option><option value="name">Name A–Z</option><option value="email">Email A–Z</option></select>
  ${isAdmin()?'<button onclick="openForm()">+ Add student</button>':''}</div><div class="wrap" id="tbl"></div><div id="pg"></div></main><div id="modal"></div>`;
  $('#sort').value=st.sort;
  $('#q').oninput=e=>{clearTimeout(tm);tm=setTimeout(()=>{st.q=e.target.value;st.page=0;load()},300)};
  $('#sort').onchange=e=>{st.sort=e.target.value;st.page=0;load()};
  try{rooms=await api('/rooms')}catch(e){}
  load()}
async function load(){const t=$('#tbl');if(!t)return;t.innerHTML='<div class="msg">Loading…</div>';
  try{const d=await api(`/students?page=${st.page}&size=${st.size}&sort=${st.sort}&q=${encodeURIComponent(st.q)}`);
    if(!d.content.length&&st.page>0){st.page--;return load()}
    st.data=d;
    t.innerHTML=d.content.length?`<table><tr><th>Name</th><th>Email</th><th>Phone</th><th>Room</th>${isAdmin()?'<th></th>':''}</tr>`+
      d.content.map(s=>`<tr><td>${esc(s.name)}</td><td>${esc(s.email)}</td><td>${esc(s.phone)}</td><td>${esc(s.roomNo||'—')}</td>
      ${isAdmin()?`<td><button class="s" onclick="openForm(${s.id})">Edit</button><button class="d" onclick="confirmDel(${s.id})">Delete</button></td>`:''}</tr>`).join('')+'</table>'
      :'<div class="msg">No students found.'+(isAdmin()?' Add the first one with “Add student”.':'')+'</div>';
    $('#pg').innerHTML=`<button class="s" ${st.page<=0?'disabled':''} onclick="go(-1)">Previous</button><span>Page ${st.page+1} of ${Math.max(d.totalPages,1)} (${d.totalElements})</span>
      <button class="s" ${st.page+1>=d.totalPages?'disabled':''} onclick="go(1)">Next</button>`}
  catch(e){t.innerHTML=`<div class="msg">${esc(e.message)}<br><br><button onclick="load()">Retry</button></div>`}}
function go(n){st.page+=n;load()}
function modal(h){$('#modal').innerHTML=`<div class="ov"><div class="card m">${h}</div></div>`}
function closeModal(){$('#modal').innerHTML=''}

function openForm(id){const s=id?st.data.content.find(x=>x.id===id):{name:'',email:'',phone:'',roomId:null};
  modal(`<h3>${id?'Edit student':'Add student'}</h3><form id="sf" novalidate>
  <label>Name<input name="name" value="${esc(s.name)}"><small class="err" data-f="name"></small></label>
  <label>Email<input name="email" value="${esc(s.email)}"><small class="err" data-f="email"></small></label>
  <label>Phone (10 digits)<input name="phone" value="${esc(s.phone)}"><small class="err" data-f="phone"></small></label>
  <label>Room<select name="roomId"><option value="">No room</option>${rooms.map(r=>`<option value="${r.id}" ${r.id===s.roomId?'selected':''}>${esc(r.roomNo)} (${r.occupied}/${r.capacity})</option>`).join('')}</select></label>
  <small class="err" id="ferr"></small><button id="sb">Save</button> <button type="button" class="s" onclick="closeModal()">Cancel</button></form>`);
  $('#sf').onsubmit=async e=>{e.preventDefault();const d=Object.fromEntries(new FormData(e.target)),er={};
    if(!d.name.trim())er.name='Name is required';
    if(!/^\S+@\S+\.\S+$/.test(d.email))er.email='Enter a valid email';
    if(!/^\d{10}$/.test(d.phone))er.phone='Phone must be 10 digits';
    if(showErrors(er))return;d.roomId=d.roomId?+d.roomId:null;
    const b=$('#sb');b.disabled=true;b.textContent='Saving…';
    try{await api(id?'/students/'+id:'/students',{method:id?'PUT':'POST',body:JSON.stringify(d)});
      closeModal();toast('Student saved');rooms=await api('/rooms').catch(()=>rooms);load()}
    catch(x){showErrors(x.errors||{},x.message);b.disabled=false;b.textContent='Save'}}}
function confirmDel(id){const s=st.data.content.find(x=>x.id===id);
  modal(`<h3>Delete student?</h3><p>${esc(s.name)} will be removed permanently.</p><small class="err" id="ferr"></small>
  <button class="d" id="sb" onclick="doDel(${id})">Delete</button> <button class="s" onclick="closeModal()">Cancel</button>`)}
async function doDel(id){const b=$('#sb');b.disabled=true;b.textContent='Deleting…';
  try{await api('/students/'+id,{method:'DELETE'});closeModal();toast('Student deleted');load()}
  catch(x){$('#ferr').textContent=x.message;b.disabled=false;b.textContent='Delete'}}
