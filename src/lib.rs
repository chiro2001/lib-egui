use crate::painter::{EguiPainter, PainterBeforeHandler, PainterMeshHandler, PainterVoidHandler};
use crate::state::EguiStateHandler;
use crate::utils::egui_cast;
use std::fmt::Debug;
use std::sync::{Arc, Mutex};
use std::thread::sleep;
use std::time::{Duration, Instant};
use egui::{ClippedPrimitive, TexturesDelta};
use tracing::{debug, info, trace};

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
    pub frame_count: u64,
}

impl Egui {
    pub fn new(painter: EguiPainter) -> Self {
        Self {
            painter: Arc::new(Mutex::new(painter)),
            state: Arc::new(Mutex::new(Default::default())),
            quit: Arc::new(Mutex::new(false)),
            quit_done: Arc::new(Mutex::new(false)),
            frame_count: 0,
        }
    }
    pub fn paint(&self, texture_delta: &TexturesDelta, primitives: Vec<ClippedPrimitive>) {
        {
            let painter = self.painter.lock().unwrap();
            for primitive in primitives.into_iter() {
                painter.paint_primitive(primitive);
            }
        }
    }
}

#[no_mangle]
pub extern "C" fn egui_create(before: *const (), mesh: *const (), after: *const ()) -> *const Egui {
    tracing_subscriber::fmt::init();
    debug!("creating(handler: {:?})...", mesh);
    let before: PainterBeforeHandler = unsafe { std::mem::transmute(before) };
    let mesh: PainterMeshHandler = unsafe { std::mem::transmute(mesh) };
    let after: PainterVoidHandler = unsafe { std::mem::transmute(after) };
    // let e = &Egui::new(handler);
    let painter = EguiPainter::new(before, mesh, after);
    let e = Box::new(Egui::new(painter));
    let e = Box::leak(e);
    debug!("return instance at: {:?}", e as *const Egui);
    e
}

fn egui_running(ui: &mut Egui) {
    let ctx = egui::Context::default();
    info!("egui_running start");
    loop {
        ui.frame_count += 1;
        trace!("egui frame: {}", ui.frame_count);
        let start_time = Instant::now();
        let mut state = ui.state.lock().unwrap();
        state.input.time = Some(start_time.elapsed().as_secs_f64());
        ctx.begin_frame(state.input.take());

        let full_output = ctx.run(state.input.clone(), |ctx| {
            egui::CentralPanel::default().show(&ctx, |ui| {
                ui.centered_and_justified(|ui| {
                    if ui.button("button").clicked() {
                        ui.label("clicked!");
                    } else {
                        ui.label("lib-egui");
                    }
                });
            });
        });

        let output = ctx.end_frame();
        let shapes = output.shapes;
        let primitives = ctx.tessellate(shapes);
        ui.paint(&full_output.textures_delta, primitives);

        // TODO: full_output.platform_output

        // sleep(Duration::from_millis(100));
        sleep(Duration::from_millis(1));
        let quit = ui.quit.lock().unwrap();
        if *quit {
            info!("egui_running will quit!");
            break;
        }
    }
    let mut quit_done = ui.quit_done.lock().unwrap();
    *quit_done = true;
    info!("egui_running quit");
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *mut Egui) {
    let ui = egui_cast(g);
    info!("before egui_run start");
    // std::thread::spawn(move || futures::executor::block_on(egui_running(ui)));
    // std::thread::spawn(move || futures::executor::block_on(async {
    //     egui_running(ui);
    // }));
    std::thread::spawn(move || {
        egui_running(ui);
    });
    info!("after egui_run");
}

#[no_mangle]
pub unsafe extern "C" fn egui_run_block(g: *mut Egui) {
    let ui = egui_cast(g);
    egui_running(ui);
    info!("egui_run_block done");
}

#[no_mangle]
pub unsafe extern "C" fn egui_quit(g: *mut Egui) {
    info!("egui_quit...");
    let ui = egui_cast(g);
    {
        // must first release `quit`
        let mut quit = ui.quit.lock().unwrap();
        *quit = true;
        info!("set quit flag");
    }
    let mut count = 0u64;
    let delay_ms = 1000 / 10;
    let d = 10u64;
    loop {
        sleep(Duration::from_millis(d));
        info!("getting quit_done flag...");
        let quit_done = ui.quit_done.lock().unwrap();
        if *quit_done {
            info!("normally quit process");
            break;
        }
        count += d;
        if count >= delay_ms {
            info!("wait timeout, quit process");
            break;
        }
    }
    unsafe {
        drop(Box::from_raw(g));
    }
}
