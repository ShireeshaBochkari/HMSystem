// Shared helpers, auth state, API client
const $=s=>document.querySelector(s);
const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
let auth={token:localStorage.token,user:localStorage.user,role:localStorage.role};   // auth context
const isOwner=()=>auth.role==='OWNER'||auth.role==='ADMIN';
const isAdmin=isOwner;
const isHosteler=()=>auth.role==='HOSTELER'||auth.role==='STUDENT';
let st={page:0,size:5,sort:'id',q:'',data:null}, rooms=[], tm;

function toast(m,bad){const t=$('#toast');t.textContent=m;t.className=(bad?'bad ':'')+'show';clearTimeout(t.h);t.h=setTimeout(()=>t.className=bad?'bad':'',2600)}
function logout(){localStorage.clear();auth={};location.hash='#/login';route()}
async function api(path,opt={}){
  let r;
  try{r=await fetch(API_BASE+'/api'+path,{...opt,headers:{'Content-Type':'application/json',...(auth.token?{Authorization:'Bearer '+auth.token}:{})}})}
  catch(e){throw{message:'Cannot reach the server. Check your connection and retry.'}}
  if(r.status===401&&auth.token){logout();throw{message:'Session expired. Please login again.'}}
  const t=await r.text();let d=null;try{d=t?JSON.parse(t):null}catch(e){}
  if(!r.ok)throw d||{message:'Request failed ('+r.status+')'};return d}
function showErrors(er,msg){document.querySelectorAll('.err').forEach(e=>e.textContent='');
  for(const k in er){const e=document.querySelector(`[data-f="${k}"]`);if(e)e.textContent=er[k]}
  const g=$('#ferr');if(g)g.textContent=msg||'';return Object.keys(er).length>0}
