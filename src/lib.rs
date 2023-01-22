use crate::painter::{EguiPainter, MeshPainterHandler};
use crate::state::EguiStateHandler;
use crate::utils::egui_cast;
use std::fmt::Debug;
use std::sync::{Arc, Mutex};
use std::thread::sleep;
use std::time::Duration;

mod basic;
pub mod events;
pub mod painter;
pub mod state;
mod utils;

#[derive(Debug)]
pub struct Egui {
    pub painter: Arc<Mutex<EguiPainter>>,
    pub state: Arc<Mutex<EguiStateHandler>>,
    pub quit: Arc<Mutex<bool>>,
    pub quit_done: Arc<Mutex<bool>>,
}

impl Egui {
    pub fn new(handler: MeshPainterHandler) -> Self {
        Self {
            painter: Arc::new(Mutex::new(EguiPainter::new(handler))),
            state: Arc::new(Mutex::new(Default::default())),
            quit: Arc::new(Mutex::new(false)),
            quit_done: Arc::new(Mutex::new(false)),
        }
    }
}

#[no_mangle]
pub extern "C" fn egui_create(handler: *const ()) -> *const Egui {
    println!("creating(handler: {:?})...", handler);
    let handler: MeshPainterHandler = unsafe { std::mem::transmute(handler) };
    // let e = &Egui::new(handler);
    let e = Box::new(Egui::new(handler));
    let e = Box::leak(e);
    println!("return instance at: {:?}", e as *const Egui);
    e
}

async fn egui_running(ui: &Egui) {
    loop {
        println!("egui thread running");
        sleep(Duration::from_millis(300));
        let quit = ui.quit.lock().unwrap();
        if *quit {
            println!("egui_running will quit!");
            break;
        }
    }
    let mut quit_done = ui.quit_done.lock().unwrap();
    *quit_done = true;
    println!("egui_running quit");
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *mut Egui) {
    let ui = egui_cast(g);
    std::thread::spawn(move || futures::executor::block_on(egui_running(ui)));
}

#[no_mangle]
pub unsafe extern "C" fn egui_run_block(g: *mut Egui) {
    let ui = egui_cast(g);
    futures::executor::block_on(egui_running(ui));
}

#[no_mangle]
pub unsafe extern "C" fn egui_quit(g: *mut Egui) {
    println!("egui_quit...");
    let ui = egui_cast(g);
    {
        // must first release `quit`
        let mut quit = ui.quit.lock().unwrap();
        *quit = true;
        println!("set quit flag");
    }
    loop {
        sleep(Duration::from_millis(10));
        println!("getting quit_done flag...");
        let quit_done = ui.quit_done.lock().unwrap();
        if *quit_done {
            break;
        }
    }
    unsafe {
        drop(Box::from_raw(g));
    }
}
