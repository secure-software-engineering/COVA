'use strict';
import * as net from 'net';
import * as path from 'path';
import { workspace, ExtensionContext, window, TreeDataProvider,EventEmitter, Event, TreeItem, ProviderResult,Uri,commands, TreeItemCollapsibleState, TextEditorRevealType, Selection, Position, TextEditorDecorationType } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo, Command, Range} from 'vscode-languageclient';

// this method is called when your extension is activated
// your extension is activated the very first time the command is executed
export async function activate(context: ExtensionContext) {
		// Startup options for the language server
	const lspTransport = workspace.getConfiguration().get("covaide.lspTransport","socket")
	
	let script = 'java';
    let args = ['-jar',context.asAbsolutePath(path.join('covaIDE-0.0.1-SNAPSHOT.jar'))];
   
	
	const serverOptionsStdio = {
		run : { command: script, args: args },
        debug: { command: script, args: args} //, options: { env: createDebugEnv() }
	}

	const deco = window.createTextEditorDecorationType({
		textDecoration: 'underline'
	});

	const decoLocations  = [];
	
    const serverOptionsSocket = () => {
		const socket = net.connect({ port: 5007 })
		const result: StreamInfo = {
			writer: socket,
			reader: socket
		}
		return new Promise<StreamInfo>((resolve) => {
			socket.on("connect", () => resolve(result))
			socket.on("error", _ =>
				window.showErrorMessage(
					"Failed to connect to COVA-IDE language server. Make sure that the language server is running " +
					"-or- configure the extension to connect via standard IO."))
		})
	}
	
	const serverOptions: ServerOptions =
		(lspTransport === "socket") ? serverOptionsSocket : (lspTransport === "stdio") ? serverOptionsStdio : null
			
    let clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'java' }],
        synchronize: {
            configurationSection: 'java',
            fileEvents: [ workspace.createFileSystemWatcher('**/*.java') ]
        }
	};
	// Register commands
	commands.registerCommand("covaIDE.goto", async (args: TreeViewNode) => {
			try {
				const location = args.command.arguments[0];
				const fileUri = location.uri;
				const doc = await workspace.openTextDocument(Uri.parse(fileUri))
				const editor = await window.showTextDocument(doc, {
					preserveFocus: true,
					preview: false
				})
				const start = location.range.start;
				const end = location.range.end;
				window.activeTextEditor.selection = new Selection(new Position(start.line, start.character), new Position(end.line, end.character));
				decoLocations.push(location.range);
				editor.setDecorations(deco, decoLocations);
				editor.revealRange(location.range, TextEditorRevealType.InCenter);
			} catch (e) {
				window.showErrorMessage(e)
			}
		});

	//setup tree view
	let constraintInfoProvider=new SimpleTreeDataProvider();
	window.registerTreeDataProvider("covaIDE.constraintInfo",constraintInfoProvider);
    // Create the language client and start the client.
    let client : LanguageClient = new LanguageClient('COVA-IDE','COVA-IDE', serverOptions, clientOptions);
    client.start();
	await client.onReady();
	client.onNotification("covaIDE/constraintInfo", handleTreeDataNotification(constraintInfoProvider,"covaIDE.constraintInfo",deco, decoLocations));
	
}


export interface PublishConstraintParams{
	viewId: string
	constraint: string
	items: TreeViewNode[]
	command: Command
}


export class TreeViewNode extends TreeItem {
	range: Range;
	children: TreeViewNode[] = new Array();
}


export class SimpleTreeDataProvider implements TreeDataProvider<TreeViewNode> {
	emitter = new EventEmitter<TreeViewNode>();
	onDidChangeTreeData?: Event<TreeViewNode> = this.emitter.event;
	rootItems: Array<TreeViewNode>;

	update(constraint: string, command: Command, items: TreeViewNode[],viewId: String) {
		this.rootItems= new Array();
		var constraintItem = new TreeViewNode(constraint);
		this.rootItems.push(constraintItem);
		var children=new Array();
		for( var i =0; i< items.length; i++)
		{
			let item= items[i];
			let child: TreeViewNode = new TreeViewNode(item.label);
			child.collapsibleState= item.collapsibleState;
			child.resourceUri=item.resourceUri;
			child.children=item.children;
			child.command = item.command;
			child.iconPath = path.join(__filename, '..','..','media/info.svg');
			children.push(child)
		}
		constraintItem.command=command;
		constraintItem.children=children;
		constraintItem.label=constraint;
		constraintItem.iconPath = path.join(__filename, '..','..','media/summary.svg');
		constraintItem.collapsibleState=TreeItemCollapsibleState.Expanded;
		this.emitter.fire()
	}

	getTreeItem(element: TreeViewNode): TreeItem | Thenable<TreeItem> {
		if (typeof element.resourceUri === 'string')
			element.resourceUri = Uri.parse(element.resourceUri)
		if (typeof element.iconPath === 'string' && element.iconPath.startsWith("~"))
			element.iconPath = path.join(__filename, "..", "..", "..", element.iconPath.substr(1))
		return element
	}

	getChildren(element?: TreeViewNode): ProviderResult<TreeViewNode[]> {
		if (element)
			return element.children
		return this.rootItems
	}

	getParent(element: TreeViewNode): ProviderResult<TreeViewNode> {
		function* allElements(parent: TreeViewNode, children: TreeViewNode[]): IterableIterator<{ parent: TreeViewNode, child: TreeViewNode }> {
			for (const child of children) {
				yield { parent, child }
				yield* allElements(child, child.children)
			}
		}

		for (const o of allElements(null, this.rootItems))
			if (o.child === element)
				return o.parent
	}
}

export function handleTreeDataNotification(dataProvider:  SimpleTreeDataProvider, viewId: string, deco: TextEditorDecorationType, ops: any[]) {
	return (args: PublishConstraintParams) => {
		if (dataProvider)
		{
			while(ops.length > 0) {
				ops.pop();
			}
			window.activeTextEditor.setDecorations(deco, ops);
			dataProvider.update(args.constraint, args.command, args.items, viewId);
		}
	}

}