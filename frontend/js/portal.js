const portalTitles={dashboard:'Overview',messages:'Messages',fees:'Fees',meals:'Meal schedule',complaints:'Complaints'};
const dateLabel=value=>value?new Date(value).toLocaleString():'—';
const money=value=>new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR'}).format(Number(value||0));

function portalView(section){
  $('#app').innerHTML=nav()+`<main class="portal"><div class="page-heading"><div><p class="eyebrow">${isOwner()?'OWNER CONSOLE':'HOSTELER PORTAL'}</p><h1>${portalTitles[section]}</h1></div></div><div id="portal-content"><div class="msg">Loading…</div></div></main>`;
  if(section==='dashboard')return dashboardView();
  if(section==='messages')return messagesView();
  if(section==='fees')return feesView();
  if(section==='meals')return mealsView();
  return complaintsView();
}

function stats(items){return`<div class="stat-grid">${items.map(([label,value])=>`<div class="stat"><span>${label}</span><strong>${value}</strong></div>`).join('')}</div>`}

async function dashboardView(){
  const root=$('#portal-content');
  try{
    if(isOwner()){
      const [hostelers,roomList,feeList,complaintList]=await Promise.all([api('/hostelers'),api('/rooms'),api('/fees'),api('/complaints')]);
      root.innerHTML=stats([['Hostelers',hostelers.length],['Rooms',roomList.length],['Payments to review',feeList.filter(f=>f.status==='PAYMENT_PENDING').length],['Open complaints',complaintList.filter(c=>c.status!=='RESOLVED').length]])+
        `<div class="portal-panel"><h2>Owner tasks</h2><p>Assign rooms, send resident messages, review fee payments, update meal times, and respond to complaints.</p><div class="quick-links"><a href="#/students">Manage hostelers</a><a href="#/fees">Review fees</a><a href="#/complaints">View complaints</a></div></div>`;
    }else{
      const [me,feeList,messageList,complaintList,schedule]=await Promise.all([api('/me'),api('/fees'),api('/messages'),api('/complaints'),api('/schedule')]);
      const currentDay=new Date().toLocaleDateString('en-US',{weekday:'long'}).toUpperCase();
      const today=schedule.filter(m=>m.dayOfWeek===currentDay);
      root.innerHTML=`<section class="welcome"><div><p class="eyebrow">YOUR ROOM</p><h2>${esc(me.roomNo||'Not assigned yet')}</h2><p>${esc(me.name)} · ${esc(me.email)}</p></div><a href="#/meals">Today’s meal times</a></section>`+
        stats([['Unpaid fees',feeList.filter(f=>f.status!=='PAID').length],['New messages',messageList.length],['Active complaints',complaintList.filter(c=>c.status!=='RESOLVED').length]])+
        `<div class="portal-panel"><h2>${today.length?'Today’s meals':'Meal schedule'}</h2>${today.length?`<div class="meal-list">${today.map(m=>`<p><strong>${esc(m.meal)}</strong><time>${esc(m.time)}</time><span>${esc(m.menu)}</span></p>`).join('')}</div>`:'<p>No meals are listed for today.</p>'}</div>`;
    }
  }catch(e){root.innerHTML=`<div class="msg">${esc(e.message)}</div>`}
}

async function messagesView(){
  const root=$('#portal-content');
  try{
    const [items,hostelers]=await Promise.all([api('/messages'),isOwner()?api('/hostelers'):Promise.resolve([])]);
    root.innerHTML=`${isOwner()?`<form class="portal-panel portal-form" id="message-form"><h2>Send a message</h2><label>Hosteler<select name="recipientId" required><option value="">Choose a hosteler</option>${hostelers.map(h=>`<option value="${h.id}">${esc(h.name)} · ${esc(h.username||h.email)}</option>`).join('')}</select></label><label>Subject<input name="title" maxlength="100" required></label><label>Message<textarea name="body" maxlength="4000" rows="4" required></textarea></label><button>Send message</button></form>`:''}
      <section class="portal-panel"><h2>${isOwner()?'Sent messages':'Inbox'}</h2>${items.length?`<div class="message-list">${items.map(m=>`<article class="message-item"><div><h3>${esc(m.title)}</h3><time>${dateLabel(m.sentAt)}</time></div>${isOwner()?`<p class="muted">To ${esc(m.recipient)}</p>`:''}<p>${esc(m.body)}</p></article>`).join('')}</div>`:'<p class="empty">No messages yet.</p>'}</section>`;
    const form=$('#message-form');if(form)form.onsubmit=async e=>{e.preventDefault();const data=Object.fromEntries(new FormData(form));data.recipientId=Number(data.recipientId);try{await api('/messages',{method:'POST',body:JSON.stringify(data)});toast('Message sent');messagesView()}catch(x){toast(x.message,true)}};
  }catch(e){root.innerHTML=`<div class="msg">${esc(e.message)}</div>`}
}

async function feesView(){
  const root=$('#portal-content');
  try{
    const [items,hostelers]=await Promise.all([api('/fees'),isOwner()?api('/hostelers'):Promise.resolve([])]);
    root.innerHTML=`${isOwner()?`<form class="portal-panel portal-form" id="fee-form"><h2>Create fee</h2><label>Hosteler<select name="studentId" required><option value="">Choose a hosteler</option>${hostelers.map(h=>`<option value="${h.id}">${esc(h.name)} · ${esc(h.username||h.email)}</option>`).join('')}</select></label><div class="form-row"><label>Period<input name="period" placeholder="October 2026" maxlength="40" required></label><label>Amount<input name="amount" type="number" min="0.01" step="0.01" required></label><label>Due date<input name="dueDate" type="date" required></label></div><button>Create fee</button></form>`:''}
      <section class="portal-panel"><h2>${isOwner()?'Fee ledger':'Your fees'}</h2>${items.length?`<div class="wrap"><table><thead><tr>${isOwner()?'<th>Hosteler</th>':''}<th>Period</th><th>Amount</th><th>Due</th><th>Status</th><th></th></tr></thead><tbody>${items.map(f=>`<tr>${isOwner()?`<td>${esc(f.hosteler)}</td>`:''}<td>${esc(f.period)}</td><td>${money(f.amount)}</td><td>${esc(f.dueDate)}</td><td><span class="status ${esc(f.status.toLowerCase())}">${esc(f.status.replace('_',' '))}</span></td><td>${isOwner()&&f.status==='PAYMENT_PENDING'?`<button class="s" onclick="reviewFee(${f.id},'PAID')">Confirm paid</button><button class="d" onclick="reviewFee(${f.id},'DECLINED')">Decline</button>`:!isOwner()&&f.status!=='PAID'?`<button class="s" onclick="submitFee(${f.id})">Submit payment</button>`:''}</td></tr>`).join('')}</tbody></table></div>`:'<p class="empty">No fee records yet.</p>'}</section><p class="note">Payment requests are recorded here for the owner to verify. This app does not connect to a payment gateway.</p>`;
    const form=$('#fee-form');if(form)form.onsubmit=async e=>{e.preventDefault();const data=Object.fromEntries(new FormData(form));data.studentId=Number(data.studentId);data.amount=Number(data.amount);try{await api('/fees',{method:'POST',body:JSON.stringify(data)});toast('Fee created');feesView()}catch(x){toast(x.message,true)}};
  }catch(e){root.innerHTML=`<div class="msg">${esc(e.message)}</div>`}
}

async function submitFee(id){try{await api('/fees/'+id+'/payment',{method:'POST'});toast('Payment submitted for review');feesView()}catch(e){toast(e.message,true)}}
async function reviewFee(id,status){try{await api('/fees/'+id+'/verify',{method:'PUT',body:JSON.stringify({status})});toast(status==='PAID'?'Payment confirmed':'Payment declined');feesView()}catch(e){toast(e.message,true)}}

async function mealsView(){
  const root=$('#portal-content');
  try{
    const items=await api('/schedule');
    const weekdayOrder=['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
    items.sort((a,b)=>weekdayOrder.indexOf(a.dayOfWeek)-weekdayOrder.indexOf(b.dayOfWeek)||a.time.localeCompare(b.time));
    root.innerHTML=`${isOwner()?`<form class="portal-panel portal-form" id="meal-form"><h2>Add meal time</h2><div class="form-row"><label>Day<select name="dayOfWeek">${['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'].map(d=>`<option>${d}</option>`).join('')}</select></label><label>Meal<select name="meal"><option>BREAKFAST</option><option>LUNCH</option><option>DINNER</option><option>SNACK</option></select></label><label>Time<input name="time" type="time" required></label></div><label>Menu<input name="menu" maxlength="500" required></label><button>Save meal time</button></form>`:''}
      <section class="portal-panel"><h2>Weekly schedule</h2>${items.length?`<div class="wrap"><table><thead><tr><th>Day</th><th>Meal</th><th>Time</th><th>Menu</th>${isOwner()?'<th></th>':''}</tr></thead><tbody>${items.map(m=>`<tr><td>${esc(m.dayOfWeek)}</td><td>${esc(m.meal)}</td><td>${esc(m.time)}</td><td>${esc(m.menu)}</td>${isOwner()?`<td><button class="d" onclick="deleteMeal(${m.id})">Remove</button></td>`:''}</tr>`).join('')}</tbody></table></div>`:'<p class="empty">No meal times have been posted.</p>'}</section>`;
    const form=$('#meal-form');if(form)form.onsubmit=async e=>{e.preventDefault();const data=Object.fromEntries(new FormData(form));try{await api('/schedule',{method:'POST',body:JSON.stringify(data)});toast('Meal time saved');mealsView()}catch(x){toast(x.message,true)}};
  }catch(e){root.innerHTML=`<div class="msg">${esc(e.message)}</div>`}
}

async function deleteMeal(id){if(!confirm('Remove this meal time?'))return;try{await api('/schedule/'+id,{method:'DELETE'});toast('Meal time removed');mealsView()}catch(e){toast(e.message,true)}}

async function complaintsView(){
  const root=$('#portal-content');
  try{
    const items=await api('/complaints');
    root.innerHTML=`${!isOwner()?`<form class="portal-panel portal-form" id="complaint-form"><h2>Submit a complaint</h2><label>Subject<input name="title" maxlength="120" required></label><label>Details<textarea name="details" maxlength="4000" rows="4" required></textarea></label><button>Submit complaint</button></form>`:''}
      <section class="portal-panel"><h2>${isOwner()?'Hostel complaints':'Your complaints'}</h2>${items.length?`<div class="complaint-list">${items.map(c=>`<article class="complaint-item"><div class="item-heading"><div>${isOwner()?`<p class="muted">${esc(c.hosteler)}</p>`:''}<h3>${esc(c.title)}</h3></div><span class="status ${esc(c.status.toLowerCase())}">${esc(c.status.replace('_',' '))}</span></div><p>${esc(c.details)}</p>${c.response?`<p class="response"><strong>Owner response:</strong> ${esc(c.response)}</p>`:''}<time>${dateLabel(c.createdAt)}</time>${isOwner()?`<form class="review-form" onsubmit="updateComplaint(event,${c.id})"><select name="status"><option ${c.status==='OPEN'?'selected':''}>OPEN</option><option ${c.status==='IN_PROGRESS'?'selected':''}>IN_PROGRESS</option><option ${c.status==='RESOLVED'?'selected':''}>RESOLVED</option></select><input name="response" maxlength="2000" value="${esc(c.response||'')}" placeholder="Reply to hosteler"><button class="s">Update</button></form>`:''}</article>`).join('')}</div>`:'<p class="empty">No complaints yet.</p>'}</section>`;
    const form=$('#complaint-form');if(form)form.onsubmit=async e=>{e.preventDefault();const data=Object.fromEntries(new FormData(form));try{await api('/complaints',{method:'POST',body:JSON.stringify(data)});toast('Complaint submitted');complaintsView()}catch(x){toast(x.message,true)}};
  }catch(e){root.innerHTML=`<div class="msg">${esc(e.message)}</div>`}
}

async function updateComplaint(event,id){event.preventDefault();const data=Object.fromEntries(new FormData(event.currentTarget));try{await api('/complaints/'+id,{method:'PUT',body:JSON.stringify(data)});toast('Complaint updated');complaintsView()}catch(e){toast(e.message,true)}}