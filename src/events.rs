use crate::utils::egui_cast;
use crate::Egui;
use egui::{pos2, Event, PointerButton};

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
    let pixels_per_point = ui.painter.pixels_per_point;
    ui.state.pointer_pos = pos2(x as f32 / pixels_per_point, y as f32 / pixels_per_point);
    ui.state
        .input
        .events
        .push(Event::PointerMoved(ui.state.pointer_pos));
}
#[no_mangle]
pub extern "C" fn egui_event_pointer_button(g: *mut Egui, button: u32, pressed: bool) {
    let ui = egui_cast(g);
    let button = pointer_button_from_u32(button);
    if let Some(button) = button {
        ui.state.input.events.push(egui::Event::PointerButton {
            pos: ui.state.pointer_pos,
            button,
            pressed,
            modifiers: ui.state.modifiers,
        })
    }
}
