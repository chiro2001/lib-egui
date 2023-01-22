use crate::utils::egui_cast;
use crate::Egui;
use egui::{pos2, Event, PointerButton};
use std::ops::Deref;

fn pointer_button_from_u32(id: u32) -> Option<PointerButton> {
    use PointerButton::*;
    match id {
        0 => Some(Primary),
        1 => Some(Secondary),
        2 => Some(Middle),
        3 => Some(Extra1),
        4 => Some(Extra2),
        _ => None,
    }
}

#[no_mangle]
pub extern "C" fn egui_event_pointer_move(g: *mut Egui, x: i32, y: i32) {
    let ui = egui_cast(g);
    let painter = ui.painter.lock().unwrap();
    let mut state = ui.state.lock().unwrap();
    let pixels_per_point = painter.deref().pixels_per_point;
    let pos = pos2(x as f32 / pixels_per_point, y as f32 / pixels_per_point);
    state.pointer_pos = pos.clone();
    state.input.events.push(Event::PointerMoved(pos));
}

#[no_mangle]
pub extern "C" fn egui_event_pointer_button(g: *mut Egui, button: u32, pressed: bool) {
    let ui = egui_cast(g);
    let button = pointer_button_from_u32(button);
    let mut state = ui.state.lock().unwrap();
    if let Some(button) = button {
        let pos = state.pointer_pos;
        let modifiers = state.modifiers;
        state.input.events.push(egui::Event::PointerButton {
            pos,
            button,
            pressed,
            modifiers,
        })
    }
}
