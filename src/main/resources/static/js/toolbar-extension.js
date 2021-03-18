function ToolbarExtension(viewer, options) {
    Autodesk.Viewing.Extension.call(this, viewer, options);
}
ToolbarExtension.prototype = Object.create(Autodesk.Viewing.Extension.prototype);
Autodesk.Viewing.theExtensionManager.registerExtension('ToolbarExtension', ToolbarExtension);

let panel = null;
let panel2 = null;
ToolbarExtension.prototype.onToolbarCreated = function (toolbar) {

    let viewer = this.viewer;
    panel = this.panel;
    panel2 = this.panel2;

    let revisionButton = new Autodesk.Viewing.UI.Button('show-env-bg-button');
    revisionButton.onClick = function (e) {
        if (panel === null || panel === undefined) {
            panel = new revision_panel(viewer, viewer.container, 'revision_list', 'Revision List');
            panel.setVisible(!panel.isVisible());
            getModelList();
        } else {
            panel.setVisible(!panel.isVisible());
        }
    };
    revisionButton.addClass('show-env-bg-button');
    revisionButton.setToolTip('Revision List');

    let treeButton = new Autodesk.Viewing.UI.Button('hide-env-bg-button');
    treeButton.onClick = function (e) {
        if (panel2 === null || panel2 === undefined) {
            panel2 = new TreePanel(viewer, viewer.container, 'tree_panel', 'Tree');
            panel2.setVisible(!panel2.isVisible());
            getTreeList();
        } else {
            panel2.setVisible(!panel2.isVisible());
        }
        for (let i = 0; i < models.length; i++) {
            viewer.hide(1, models[i]);
        }
        viewer.show(1, main_model);
    };
    treeButton.addClass('hide-env-bg-button');
    treeButton.setToolTip('Tree');

    this.subToolbar = new Autodesk.Viewing.UI.ControlGroup('my-custom-toolbar');
    this.subToolbar.addControl(revisionButton);
    this.subToolbar.addControl(treeButton);

    toolbar.addControl(this.subToolbar);
};


function revision_panel(viewer, container, id, title, options) {
    this.viewer = viewer;
    Autodesk.Viewing.UI.DockingPanel.call(this, container, id, title, options);

    this.container.classList.add('docking-panel-container-solid-color-a');
    this.container.style.top = '10px';
    this.container.style.left = '10px';
    this.container.style.width = 'auto';
    this.container.style.height = 'auto';
    this.container.style.resize = 'auto';

    let div = document.createElement('div');
    div.style.margin = '20px';
    this.container.appendChild(div);
}

revision_panel.prototype = Object.create(Autodesk.Viewing.UI.DockingPanel.prototype);
revision_panel.prototype.constructor = revision_panel;

function TreePanel(viewer, container, id, title, options) {
    this.viewer = viewer;
    Autodesk.Viewing.UI.DockingPanel.call(this, container, id, title, options);

    this.container.classList.add('docking-panel-container-solid-color-a');
    this.container.style.top = '10px';
    this.container.style.left = '10px';
    this.container.style.width = 'auto';
    this.container.style.height = 'auto';
    this.container.style.resize = 'auto';

    let div = document.createElement('div');
    div.style.margin = '20px';
    div.id = 'tree_panel_content';
    this.container.appendChild(div);
}

TreePanel.prototype = Object.create(Autodesk.Viewing.UI.DockingPanel.prototype);
TreePanel.prototype.constructor = TreePanel;

function getModelList() {

    models = viewer.impl.visibilityManager.models;
    $("#revision_list").children().eq(3).empty();
    $("#revision_list").children().eq(3).append("<table><tbody id='list_table'></tbody></table>");
    $("#list_table").append(
        "<tr>" +
        "<th>No</th>" +
        "<th>Date</th>" +
        "<th>IFC File</th>" +
        "<th>DAT File</th>" +
        "</tr>");

    $.ajax({
        url: './list_bucket_data',
        type: 'get',
        data: {bucketKey: bucketKey},
    }).done(function (data) {

        for (let i = 0; i < data.length; i++) {
            let title = "";
            let ifc_file = "";
            let dat_file = "";

            title = data[i].title;
            if ('ifc' === data[i].title.slice(data[i].title.indexOf(".") + 1).toLowerCase()) {
                ifc_file = data[i].title;
            } else {
                dat_file = data[i].title;
            }

            $("#list_table").append(
                "<tr onclick='selectFile(this)' data-name='" + title + "'>" +
                "<td>" + (i + 1) + "</td>" +
                "<td>" + data[i].create_time + "</td>" +
                "<td>" + ifc_file + "</td>" +
                "<td>" + dat_file + "</td>" +
                "</tr>");
        }
    });
}

function selectFile(obj) {
    let name = $(obj).data('name');
    let extension = name.slice(name.indexOf(".") + 1).toLowerCase();
    let model;

    for (let i = 0; i < models.length; i++) {
        if (1 === obj.rowIndex) {
            viewer.hide(1, models[i]); //메인
        }
        if (name === models[i].getDocumentNode().data.name) {
            model = models[i];
        }
    }

    if ('dat' === extension) {
        for (let j = 0; j < delete_id_arr.length; j++) {
            viewer.hide(delete_id_arr[j], main_model);
        }
    } else {
        if (name === main_title) {
            viewer.show(1, main_model);
        } else {
            for (let i = 0; i < main_arr.length; i++) {
                for (let j = 0; j < sub_arr.length; j++) {
                    if (sub_arr[j].title === name) {
                        if (main_arr[i].guid === sub_arr[j].guid) {
                            viewer.hide(main_arr[i].objectId, main_model);
                        } else {
                            viewer.show(main_arr[i].objectId, model);
                        }
                    }
                }
            }
        }
    }
}

function getTreeList() {

    models = viewer.impl.visibilityManager.models;
    $('#tree_panel_content').append("<div id='jstree'></div>");

    zone = Array.from(new Set(zone));
    floor = Array.from(new Set(floor)); //중복제거
    sub_zone = Array.from(new Set(sub_zone));
    zone_arr = Array.from(new Set(zone_arr));

    let treeDataSource = [];

    for (let i = 0; i < zone.length; i++) {
        if (undefined !== zone[i]) {
            let set_zone_arr = {};
            set_zone_arr.id = zone[i];
            set_zone_arr.parent = "#";
            set_zone_arr.text = zone[i];
            set_zone_arr.type = 'folder';
            set_zone_arr.state = '';
            set_zone_arr.field = zone[i];
            set_zone_arr.area = 'zone';
            treeDataSource.push(set_zone_arr);
        }
    }

    for (let i = 0; i < zone_arr.length; i++) {
        let zone_split = zone_arr[i].split(",");
        if ('undefined' !== zone_split[0]) {
            for (let j = 0; j < floor.length; j++) {
                if (zone_split[1] === floor[j]) {
                    let set_floor_arr = {};
                    set_floor_arr.id = zone_split[0] + "," + zone_split[1];
                    set_floor_arr.parent = zone_split[0];
                    set_floor_arr.text = floor[j];
                    set_floor_arr.type = 'folder';
                    set_floor_arr.state = '';
                    set_floor_arr.field = floor[j];
                    set_floor_arr.area = 'floor';
                    treeDataSource.push(set_floor_arr);
                }
            }
            for (let j = 0; j < sub_zone.length; j++) {
                if (zone_split[2] === sub_zone[j]) {
                    let set_sub_zone_arr = {};
                    set_sub_zone_arr.id = zone_split[0] + "," + zone_split[1] + "," + zone_split[2];
                    set_sub_zone_arr.parent = zone_split[0] + "," + zone_split[1];
                    set_sub_zone_arr.text = zone_split[2];
                    set_sub_zone_arr.type = 'file';
                    set_sub_zone_arr.state = '';
                    set_sub_zone_arr.field = zone_split[2];
                    set_sub_zone_arr.area = 'sub_zone';
                    treeDataSource.push(set_sub_zone_arr);
                }
            }
        }
    }

    $('#jstree').jstree({
        "core": {
            "data": treeDataSource
        },
        "types": {
            "folder": {
                "icon": "fa fa-folder"
            },
            "file": {
                "icon": "fa fa-file"
            }
        },
        "contextmenu": {
            items: function ($node) {
                return {
                    "show": {
                        "label": "<span class='show'>Show <i class='fa fa-check state-selected'></i></span>",
                        "icon": "fas fa-eye",
                        "action": function () {
                            changeState('show')
                        },
                        "_class": "show"
                    },
                    "hide": {
                        "label": "<span class='hide'>Hide <i class='fa fa-check state-selected'></i></span>",
                        "icon": "fas fa-eye-slash",
                        "action": function () {
                            changeState('hide')
                        },
                        "_class": "hide"
                    }
                }
            },
        },
        "plugins": ["types", "contextmenu"]
    }).bind('ready.jstree', function (e, data) {
        $('#jstree').jstree('open_all');
    }).bind("show_contextmenu.jstree", function (e, data) {
        let currentDirection = data.node.original.state;
        if (currentDirection) {
            let hideCheck = currentDirection == "show" ? "hide" : "show";
            $('.jstree-contextmenu li.' + hideCheck + ' i.state-selected').hide();
        } else {
            $('.jstree-contextmenu li i.state-selected').hide();
        }
    });
}

function changeState(selectState) {

    let tree_id = "";
    let tree_area = "";

    let nodeObj = $('#jstree').jstree('get_selected', true)
    let treeSource = $('#jstree').jstree(true).settings.core.data;
    if (!nodeObj[0].original.state || (nodeObj[0].original.state != selectState)) {
        $.each(treeSource, function (i, treeNode) {
            if (treeNode.id == nodeObj[0].id) {
                treeNode.state = selectState;
                treeNode.text = treeNode.field;

                tree_id = treeNode.id;
                tree_area = treeNode.area;
            }
        });
        $('#jstree').jstree(true).refresh();
    }

    for (let i = 0; i < main_properties.length; i++) {
        if ('zone' === tree_area) {
            if (tree_id === main_properties[i].ENGSOFT_ERP_SLM.ZONE) {
                if ('show' === selectState) {
                    viewer.show(main_properties[i].objectId, main_model);
                } else if ('hide' === selectState) {
                    viewer.hide(main_properties[i].objectId, main_model);
                }
            }
        } else if ('floor' === tree_area) {
            let area_split = tree_id.split(",");
            if (area_split[0] === main_properties[i].ENGSOFT_ERP_SLM.ZONE && area_split[1] === main_properties[i].ENGSOFT_ERP_SLM.FLOOR) {
                if ('show' === selectState) {
                    viewer.show(main_properties[i].objectId, main_model);
                } else if ('hide' === selectState) {
                    viewer.hide(main_properties[i].objectId, main_model);
                }
            }
        } else if ('sub_zone' === tree_area) {
            let area_split = tree_id.split(",");
            if (area_split[0] === main_properties[i].ENGSOFT_ERP_SLM.ZONE && area_split[1] === main_properties[i].ENGSOFT_ERP_SLM.FLOOR && area_split[2] === main_properties[i].ENGSOFT_ERP_SLM.SUBZONE) {
                if ('show' === selectState) {
                    viewer.show(main_properties[i].objectId, main_model);
                } else if ('hide' === selectState) {
                    viewer.hide(main_properties[i].objectId, main_model);
                }
            }
        }
    }
}